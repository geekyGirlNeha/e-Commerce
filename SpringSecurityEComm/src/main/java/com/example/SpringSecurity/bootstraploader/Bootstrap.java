package com.example.SpringSecurity.bootstraploader;

import com.example.SpringSecurity.repository.UserRepository;
import com.example.SpringSecurity.entity.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Bootstrap implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;

//    @Autowired
//    CustomerDto customerDto;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(userRepository.count()<1){
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            User user1 = new User();
            Name name = new Name();
            name.setFirst_name("Vinay");
            name.setLast_name("Parida");
            user1.setName(name);
            user1.setPassword(passwordEncoder.encode("pass"));
            user1.setRoles(Arrays.asList(new Role("ROLE_ADMIN")));
            user1.setEmail("vincdprime@gmail.com");

//            Address address = new Address();
//            address.setCity("Noida");
//            address.setAddress_line("H no. 14");
//            address.setState("UP");
//            address.setCountry("India");
//            address.setZip_code(201301);
//            address.setLabel(Label.HOME);
//
//            user1.addAddresses(address);
//            user1.addAddresses(new Address("Noida", "UP", "India", "H no 14", 201301, Label.HOME));
//            user1.addAddresses(new Address("Egypt", "EG", "Egypt","Khonshu Temple", 234244, Label.OFFICE));
            user1.setIs_active(true);

//            Customer user2 = new Customer();
//            Name name1 = new Name();
//            name1.setFirst_name("arc");
//            name1.setLast_name("dhb");
//            user2.setName(name1);
//            user2.setPassword(passwordEncoder.encode("moongod"));
//            user2.setRoles(Arrays.asList(new Role("ROLE_CUSTOMER")));
//            user2.setEmail("user2email@domain.com");
//            user2.setAddresses(Arrays.asList(new Address("Noida", "UP", "India", "H no 14", 201301, Label.HOME)));
//            user2.setContact("9971294766");
//
//            Seller user3 = new Seller();
//            user3.setName(new Name("Jake", "", "Lockley"));
//            user3.setPassword(passwordEncoder.encode("moongod"));
//            user3.setRoles(Arrays.asList(new Role("ROLE_SELLER")));
//            user3.setEmail("user3email@domain.com");
//            user3.setAddresses(Arrays.asList(new Address("Noida", "UP", "India", "H no 14", 201301, Label.HOME)));
//            user3.setCompany_contact("9824981");
//            user3.setCompany_name("TTN");

            userRepository.save(user1);
//            userRepository.save(user2);
//            userRepository.save(user3);

        }

    }
}
