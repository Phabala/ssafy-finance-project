package com.ssafy.memberserver.domain.students.sign.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.memberserver.common.enums.MemberRole;
import com.ssafy.memberserver.common.error.ErrorCode;
import com.ssafy.memberserver.common.exception.ApiException;
import com.ssafy.memberserver.domain.security.TokenProvider;
import com.ssafy.memberserver.domain.students.entity.Student;
import com.ssafy.memberserver.domain.students.repository.StudentRepository;
import com.ssafy.memberserver.domain.students.sign.dto.signIn.SignInRequest;
import com.ssafy.memberserver.domain.students.sign.dto.signIn.SignInResponse;
import com.ssafy.memberserver.domain.students.sign.dto.signUp.StudentSignUpRequest;
import com.ssafy.memberserver.domain.students.sign.dto.signUp.StudentSignUpResponse;
import com.ssafy.memberserver.domain.teachers.entity.Teacher;
import com.ssafy.memberserver.domain.teachers.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentSignService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public StudentSignUpResponse studentSignUp(StudentSignUpRequest studentSignUpRequest) {
        if (studentRepository.findByStudentId(studentSignUpRequest.getStudentId()).isPresent()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "아이디가 중복입니다.");
        }
        Student student = studentRepository.save(Student.from(studentSignUpRequest, passwordEncoder));
        studentRepository.flush();
        return StudentSignUpResponse.from(student);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest signInRequest) {
       Optional<Student> studentOptional = studentRepository.findByStudentIdAndMemberRole(signInRequest.getMemberId(), MemberRole.STUDENT);
        Optional<Teacher> teacherOptional = teacherRepository.findByTeacherIdAndMemberRole(signInRequest.getMemberId(), MemberRole.TEACHER);

        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            if (passwordEncoder.matches(signInRequest.getMemberPwd(), student.getStudentPwd())) {
                String token = tokenProvider.createToken(String.format("%s:%s", student.getStudentId(), student.getStudentName()));
                return SignInResponse.studentFrom(student, token);
            } else {
                throw new ApiException(ErrorCode.STUDENT_INVALID_INPUT, "비밀번호가 틀렸습니다");
            }
        } else if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();
            if (passwordEncoder.matches(signInRequest.getMemberPwd(), teacher.getTeacherPwd())) {
                String token = tokenProvider.TeacherCreateToken(String.format("%s:%s", teacher.getTeacherId(),teacher.getTeacherName()));
                return SignInResponse.teacherFrom(teacher, token);
            } else {
                throw new ApiException(ErrorCode.STUDENT_INVALID_INPUT, "비밀번호가 틀렸습니다");
            }
        } else {
            throw new ApiException(ErrorCode.STUDENT_INVALID_INPUT, "존재하지 않는 아이디입니다.");
        }
    }
    public boolean checkStudentIdDuplicated(String studentId){
        return studentRepository.existsByStudentId(studentId);
    }
}
