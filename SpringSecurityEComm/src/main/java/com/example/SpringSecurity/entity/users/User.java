package com.example.SpringSecurity.entity.users;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String email;

    @Embedded
    private Name name;

//    private String username;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses;

    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinTable(name = "UserRole",joinColumns=@JoinColumn( name = "UserId",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "RoleId",referencedColumnName = "id"))
    private List<Role> roles;


    private Boolean is_deleted;
    private Boolean is_active = false;
    private Boolean isAccountNotLocked = true;
    private Boolean is_enabled;

    @Lob
    private String image;

    //Getters and Setters

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean isAccountNotLocked() {
        return isAccountNotLocked;
    }

    public void setAccountNotLocked(Boolean accountNotLocked) {
        isAccountNotLocked = accountNotLocked;
    }

    public Boolean isIs_enabled() {
        return is_enabled;
    }

    public void setIs_enabled(Boolean is_unabled) {
        this.is_enabled = is_unabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddresses(Address address) {
        if(addresses == null){
            addresses = new ArrayList<>();
        }
        addresses.add(address);
        address.setUser(this);
        this.setAddresses(addresses);
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name=" + name +
                ", password='" + password + '\'' +
//                ", addresses=" + addresses +
                ", grantAuthorities=" + roles +
                ", is_deleted=" + is_deleted +
                ", is_active=" + is_active +
                '}';
    }
}

