package com.taotaoapi.auth;

import com.taotaoapi.user.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  private String firstname;
  private String lastname;
  private String username;
  private String email;
  private String password;
  private String phone;
  private String role;
}
