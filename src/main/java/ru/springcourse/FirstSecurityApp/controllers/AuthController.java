package ru.springcourse.FirstSecurityApp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.springcourse.FirstSecurityApp.dto.AuthenticationDTO;
import ru.springcourse.FirstSecurityApp.dto.PersonDTO;
import ru.springcourse.FirstSecurityApp.models.Person;
import ru.springcourse.FirstSecurityApp.security.JWTUtil;
import ru.springcourse.FirstSecurityApp.services.RegistrationService;
import ru.springcourse.FirstSecurityApp.util.ErrorResponse;
import ru.springcourse.FirstSecurityApp.util.PersonNotAddedException;
import ru.springcourse.FirstSecurityApp.util.PersonValidator;

import java.time.LocalDateTime;
import java.util.Map;

import static ru.springcourse.FirstSecurityApp.util.ErrorMessageCreator.createErrorMessage;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(PersonValidator personValidator, RegistrationService registrationService, JWTUtil jwtUtil,
                          ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.personValidator = personValidator;
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person")Person person) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public ResponseEntity<Map<String, String>> performRegistration(@RequestBody @Valid PersonDTO personDTO,
                                                          BindingResult bindingResult) {

        Person person = convertToPerson(personDTO);

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            String errorMsg = createErrorMessage(bindingResult);

            throw new PersonNotAddedException(errorMsg);
        }

        registrationService.register(person);

        String token = jwtUtil.generateToken(person.getUsername());
        return new ResponseEntity<>(Map.of("jwt-token", token), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect credentials");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return new ResponseEntity<>(Map.of("jwt-token", token), HttpStatus.OK);
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PersonNotAddedException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(BadCredentialsException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
