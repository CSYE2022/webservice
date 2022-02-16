package com.csye6225.application.endpoints;

import com.csye6225.application.objects.User;
import com.csye6225.application.objects.UserDTO;
import com.csye6225.application.repository.UserRepository;
import com.csye6225.application.security.AuthenticationRequest;
import com.csye6225.application.security.AuthenticationResponse;
import com.csye6225.application.security.MyUserDetailsService;
import com.csye6225.application.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@RestController
@RequestMapping("/v1")
public class UserAPI {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenUtils tokenUtils;


    @PostMapping(value = "/user",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity createUser(@RequestBody User user){
        System.out.println(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(userRepository.findByUsername(user.getUsername()) ==null) {
            userRepository.save(user);
            User userLatest = userRepository.findByUsername(user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(userLatest);
        }else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PutMapping(value = "/user/self",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateUser(@RequestBody User user){
        User presentUser = userRepository.findByUsername(user.getUsername());
        if(presentUser !=null){
            user.setId(presentUser.getId());
            System.out.println(user);
            userRepository.save(user);
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @PostMapping(value = "/authenticate",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest){
        System.out.println(authenticationRequest);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
        }catch (BadCredentialsException bce){
            ResponseEntity.status(404).body(null);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String jwt = tokenUtils.generrateToken(userDetails);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body( new AuthenticationResponse(jwt));
    }

    @GetMapping(value = "/user/self",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String basicToken){

        System.out.println(basicToken);
//        String token = basicToken.split(" ")[1];
        String username = tokenUtils.extractUserName(basicToken);
        User user = userRepository.findByUsername(username);
        UserDTO userDTO = UserDTO.builder().firstName(user.getFirstName()).lastName(user.getLastName()).
                account_created(user.getAccount_created()).account_updated(user.getAccount_updated()).id(user.getId())
                .username(user.getUsername()).build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body( userDTO);
    }
//    @GetMapping()
//    public ResponseEntity U getUser() {
//        return new HealthStatus("äll good");
//    }
}
