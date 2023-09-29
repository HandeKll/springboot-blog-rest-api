package com.springboot.blog.payload;

import lombok.Data;

@Data
public class SignUpDto {
    private String name;
    private String userName;
    private String email;
    private String password;
}
