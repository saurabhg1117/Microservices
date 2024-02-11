package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exception.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repository.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public User saveUser(User user) {
        String randomUserId=UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        //get user from databse with the help of user repository
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id" + userId));
        //fetch rating of the above user from RATING SERVICE
        //http://localhost:8084/ratings/users/0fc1fc06-a090-4a5a-bc6e-17cc757e0bd7
        Rating[] ratingsOfUsers = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
        logger.info("{}",ratingsOfUsers);

        List<Rating> ratings = Arrays.stream(ratingsOfUsers).toList();


        List<Rating> ratingList = ratings.stream().map(rating -> {
//            //api call to hotel service to get the hotel
//            //set the hotel to rating
//        http://localhost:8082/hotels/a964f91c-e620-4b96-8a82-6e0bc0a7013d
//        ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId() , Hotel.class);
        Hotel hotel = hotelService.getHotel(rating.getHotelId());
//        logger.info("response status code: {}",forEntity.getStatusCode());

//        set the hotel to rating
        rating.setHotel(hotel);
 //return the rating

            return rating;
        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }
}
