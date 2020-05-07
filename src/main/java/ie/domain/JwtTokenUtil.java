package ie.domain;

import ie.domain.Client;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter;
import org.json.JSONObject;
//import org.springframework.security.core.*;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import java.io.Serializable;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Base64;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JwtTokenUtil implements Serializable {

    private static JwtTokenUtil instance;

    public static JwtTokenUtil getInstance() {
        if(instance == null){
            instance = new JwtTokenUtil();
        }
        return instance;
    }

    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getAllClaimsFromToken(token);
            username =  claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expire;
        try {
            Claims claims = getAllClaimsFromToken(token);
            expire =  claims.getExpiration();
        } catch (Exception e) {
            expire = null;
        }
        return expire;
    }

    /*public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }*/

    /*public  T getClaimFromToken(String token, Function claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }*/

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey("loghme")
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Client user) {
        return doGenerateToken(user.getUsername());
    }

    private static String encode(JSONObject obj) {
        return encode(obj.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String doGenerateToken(String subject) {
        Claims claims = Jwts.claims().setSubject(subject);
        Date date = new Date();
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        calender.add(Calendar.DATE,1);
        date = calender.getTime();


        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(date)
                .setIssuedAt(new Date())
                .setIssuer("loghme.com")
                .signWith(SignatureAlgorithm.HS256 , "loghme")
                .compact();
    }

    public Boolean validateToken(String token, Client userDetails) {
        if(userDetails == null)
            return false;
        String username = getUserNameFromToken(token);
        return (
                username.equals(userDetails.getUsername())
                        && !isTokenExpired(token));
    }

}