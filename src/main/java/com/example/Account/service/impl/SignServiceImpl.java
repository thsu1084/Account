package com.example.Account.service.impl;

import com.example.Account.common.error.ErrorCode;
import com.example.Account.common.exception.ApiException;
import com.example.Account.config.security.JwtTokenProvider;
import com.example.Account.db.User;
import com.example.Account.db.repository.UserRepository;

import com.example.Account.model.SignInResultDto;
import com.example.Account.model.SignUpResultDto;
import com.example.Account.service.SignService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import static com.example.Account.common.error.CommonResponse.FAIL;
import static com.example.Account.common.error.CommonResponse.SUCCESS;


@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {



    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResultDto signUp(String id, String password, String name,String role) {

        if( userRepository.findByUid(id).isPresent() ){
            throw new ApiException(ErrorCode.UID_ALREADY_EXISTS);
        }

        
        User savedUser ;

        if (role.equalsIgnoreCase("user")) {

            savedUser = userRepository.save(User.builder()
                    .uid(id)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .role("ROLE_USER")
                .build());

        }else {
            throw new ApiException(ErrorCode.BAD_REQUEST);
        }

        if (!savedUser.getName().isEmpty()){
            return SignUpResultDto.builder()
                    .success(true)
                    .code(SUCCESS.getCode())
                    .msg(SUCCESS.getMsg())
                    .build();
        }else {
            return SignUpResultDto.builder()
                    .success(false)
                    .code(FAIL.getCode())
                    .msg(FAIL.getMsg())
                    .build();
        }


    }

    @Override
    public SignInResultDto signIn(String id, String password) throws RuntimeException {

        User user = userRepository.getByUid(id);

        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new ApiException(ErrorCode.INCORRECT_PASSWORD);
        }

        SignInResultDto signInResultDto = SignInResultDto.builder()
                .success(true)
                .msg(SUCCESS.getMsg())
                .code(SUCCESS.getCode())
                .token(jwtTokenProvider.createToken(String.valueOf(user.getUid())
                ,user.getRole()))
                .build();

        return signInResultDto;
    }
}
