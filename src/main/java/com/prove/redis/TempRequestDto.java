package com.prove.redis;

public record TempRequestDto(){
    public record TempSignUpDto(String name){}
    public static Temp toEntity(TempSignUpDto request){
        System.out.println(request.name);
        return Temp
                .builder()
                .name(request.name)
                .build();
    }
}
