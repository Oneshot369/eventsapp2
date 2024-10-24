package com.shadsluiter.eventsapp.models;

public class Converters {
    
    public static UserEntity userModelToUserEntity(UserModel userModel) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(Long.parseLong(userModel.getId()));
        userEntity.setUserName(userModel.getUserName());
        userEntity.setPassword(userModel.getPassword());
        return userEntity;
    }

    public static UserModel userEntityToUserModel(UserEntity userEntity) {
        UserModel userModel = new UserModel();
        userModel.setId(String.valueOf(userEntity.getId()));
        userModel.setUserName(userEntity.getUserName());
        userModel.setPassword(userEntity.getPassword());
        return userModel;
    }

    
}
