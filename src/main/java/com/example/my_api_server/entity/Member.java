package com.example.my_api_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity //영속성을 가진 객체로 만들겠다
@NoArgsConstructor //JPA는 기본생성자가 필수
@AllArgsConstructor //member변수를 다 받는 생성자를 만들어주는 annotation
@Table(name = "members")
@Getter
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;
    @Column
    private String password;

}
