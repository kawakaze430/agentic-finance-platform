package io.github.kawakaze430.finance;

import jakarta.persistence.*;
import java.util.UUID;
import java.time.Instant;
@Entity @Table(name="vendor")
public class Vendor {
    @Id UUID id = UUID.randomUUID();
    @Column(nullable=false) Instant createdAt;
    public Vendor() {}
public UUID getId() { return id; }
public String getName() { return name; }

@Column(nullable=false,length=120) String name;
@Column(nullable=false,length=200) String email;
@Column(nullable=false,length=3) String currency;
VendorView view() { return new VendorView(id,name,email,currency); }

}
