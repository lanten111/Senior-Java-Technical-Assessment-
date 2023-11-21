package com.makhadoni.customer.service.modules.customer.service;

import com.makhadoni.customer.service.cache.CustomerCacheService;
import com.makhadoni.customer.service.modules.customer.dto.CustomerDto;
import com.makhadoni.customer.service.exception.*;
import com.makhadoni.customer.service.modules.customer.mapper.CustomerMapper;
import com.makhadoni.customer.service.modules.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.logging.Logger;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private static final CustomerMapper mapper = CustomerMapper.MAPPER;
    private final CustomerCacheService cache;

    private static final Logger logger = Logger.getLogger(CustomerServiceImpl.class.getName());

    @Value("${spring.data.redis.timeouts.customer-list:2}")
    private int customerListCacheTimeout;

    @Value("${spring.data.redis.timeouts.customer:2}")
    private String customerCacheTimeout;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerCacheService cache) {
        this.repository = customerRepository;
        this.cache = cache;
    }

    @Override
    public Flux<CustomerDto> getCustomers(int page, int size, String firstName) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return cache.getList(createPageableCustomerKey(page, size, firstName))
                .switchIfEmpty((Flux.defer(() -> repository.findAllByFirstNameContainingIgnoreCaseOrderById(firstName, pageable)
                        .doOnNext(user -> logger.info("customer list with page "+page+" size "+size+" and first name filter of "+firstName + " not found fallback to repository"))
                        .map(mapper::mapTo)
                        .collectList()
                        .doOnNext(customerDtos -> {
                            cache.setList
                                    (createPageableCustomerKey(page, size, firstName), customerDtos, Duration.ofHours(customerListCacheTimeout)).subscribe();
                            logger.info("Cached customer list for page "+page+ " size "+size+ " with first name: "+firstName);
                        })
                        .flatMapMany(Flux::fromIterable))
                ))
                .switchIfEmpty(Flux.error(new NotFoundException("No customers found")))
                .doOnComplete(() -> logger.info("get customers Processing complete with  page "+ page  +" and size "+ size + " and name filter of "+firstName));
    }


    public Mono<CustomerDto> createOrUpdateCustomer(CustomerDto customerDto) {
        return repository.save(mapper.mapFrom(customerDto))
                .onErrorResume(DuplicateKeyException.class, ex -> Mono
                        .error(new AlreadyExistsException("Customer with email: "+ customerDto.getEmail() + " already exist")))
                .map(mapper::mapTo);
    }

    @Override
    public Mono<CustomerDto> createCustomer(CustomerDto customerDto) {
        return createOrUpdateCustomer(customerDto);
    }

    @Override
    public Mono<CustomerDto> updateCustomer(CustomerDto customerDto) {
        return createOrUpdateCustomer(customerDto);
    }

    @Override
    public Mono<CustomerDto> getCustomer(String customerId) {
       return cache.getValue(customerId)
               .switchIfEmpty(Mono.defer(() -> repository.findById(Integer.parseInt(customerId))
                       .doOnNext(user -> logger.info("customer not found in cache. Fallback to repository. Username: "+customerId))
                       .map(mapper::mapTo)
                       .doOnNext(customerDto -> {
                           cache.setValue(customerId, customerDto, Duration.ofHours(Long.parseLong(customerCacheTimeout))).subscribe();
                           logger.info("Cached user for userId: " + customerDto);
                       })
               ))
               .switchIfEmpty(Mono.defer(() -> {
                   logger.warning("Customer with id "+ customerId + " is not found");
                   return Mono.error(new NotFoundException("Customer with id "+ customerId + " does not exist"));
               }));

    }

    @Override
    public Mono<Void> deleteCustomer(int customerId) {
        return repository.deleteById(customerId)
                .doOnNext(r -> logger.warning("Deleted customer with id: "+ customerId))
                .doOnNext(t -> cache.evictAll());
    }

    private String createPageableCustomerKey(int page, int size, String firstName){
        return "getCustomers_"+ page + size + firstName;
    }
}
