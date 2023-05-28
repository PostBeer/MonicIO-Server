package com.example.monicio.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Activation token entity.
 *
 * @author Maxim Milko
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordToken {

    /**
     * The constant EXPIRATION. Default 2 hours
     */
    private static final int EXPIRATION = 2 * 60 * 60 * 1000;

    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The Token.
     */
    private String token;

    /**
     * The User.
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    /**
     * The Expiry date.
     */
    private Date expiryDate;


    /**
     * Instantiates a new Activation token.
     *
     * @param token      the token
     * @param user       the user
     * @param expiryDate the expiry date
     */
    public PasswordToken(String token, User user, Date expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = new Date(expiryDate.getTime() + EXPIRATION);
    }

    /**
     * Compare date.
     *
     * @return the boolean
     */
    public boolean compareDate() {
        return new Date().before(expiryDate);
    }
}
