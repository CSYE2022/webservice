package com.csye6225.application.model;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DynamoDBTable(tableName = "emailTokenTbl")
public class EmailToken {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String token;

    private String emailid;

}
