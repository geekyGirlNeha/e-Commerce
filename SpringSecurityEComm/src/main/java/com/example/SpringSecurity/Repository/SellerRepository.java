package com.example.SpringSecurity.Repository;

import com.example.SpringSecurity.entity.users.Seller;
import org.springframework.data.repository.CrudRepository;

public interface SellerRepository extends CrudRepository<Seller, String> {

    Seller findByGst(String gst);
}