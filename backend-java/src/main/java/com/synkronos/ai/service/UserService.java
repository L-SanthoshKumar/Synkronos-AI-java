package com.synkronos.ai.service;

import com.synkronos.ai.dto.UserDto;
import com.synkronos.ai.entity.User;
import com.synkronos.ai.repository.UserRepository;
import com.synkronos.ai.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user management operations
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user by ID
     */
    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return MapperUtil.mapToUserDto(user);
    }

    /**
     * Get user by email
     */
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return MapperUtil.mapToUserDto(user);
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserDto updateUser(String id, UserDto userDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        if (userDto.getFirstName() != null) user.setFirstName(userDto.getFirstName());
        if (userDto.getLastName() != null) user.setLastName(userDto.getLastName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());
        if (userDto.getLocation() != null) user.setLocation(userDto.getLocation());
        if (userDto.getBio() != null) user.setBio(userDto.getBio());
        if (userDto.getSkills() != null) user.setSkills(userDto.getSkills());
        if (userDto.getCurrentPosition() != null) user.setCurrentPosition(userDto.getCurrentPosition());
        if (userDto.getYearsOfExperience() != null) user.setYearsOfExperience(userDto.getYearsOfExperience());
        if (userDto.getCompanyName() != null) user.setCompanyName(userDto.getCompanyName());
        if (userDto.getCompanyWebsite() != null) user.setCompanyWebsite(userDto.getCompanyWebsite());

        user = userRepository.save(user);
        return MapperUtil.mapToUserDto(user);
    }

    /**
     * Update resume URL
     */
    @Transactional
    public UserDto updateResumeUrl(String id, String resumeUrl) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        user.setResumeUrl(resumeUrl);
        user = userRepository.save(user);
        return MapperUtil.mapToUserDto(user);
    }

    /**
     * Get all job seekers
     */
    public List<UserDto> getAllJobSeekers() {
        return userRepository.findByRole(User.UserRole.JOB_SEEKER)
            .stream()
            .map(MapperUtil::mapToUserDto)
            .collect(Collectors.toList());
    }
}

