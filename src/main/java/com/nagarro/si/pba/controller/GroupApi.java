package com.nagarro.si.pba.controller;

import com.nagarro.si.pba.dto.GroupDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface GroupApi {
    @Operation(summary = "Create a group", description = "Create a new group", tags = "Groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Group created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PostMapping
    GroupDTO create(@RequestAttribute("jwtToken") String token, @RequestBody GroupDTO groupDTO);

    @Operation(summary = "Get a group by its id", description = "Find a group by its id", tags = "Groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = GroupDTO.class))),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content)})
    @GetMapping("/{id}")
    GroupDTO getById(@PathVariable int id);

    @Operation(summary = "Get all groups", description = "Get a list of all groups", tags = "Groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of groups",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = GroupDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping
    List<GroupDTO> getAll();

    @Operation(summary = "Delete a group by its id", description = "Delete a group by its id", tags = "Groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Group deleted"),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content)})
    @DeleteMapping("/{id}")
    void delete(@PathVariable int id);

    @Operation(summary = "Join a group", description = "Join a group by its id", tags = "Groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Joined group"),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content)})
    @PutMapping("/join/{groupId}")
    void assignUserToGroup(@PathVariable int groupId, @RequestAttribute("jwtToken") String token);

    @Operation(summary = "Leave a group", description = "Leave a group by its name", tags = "Groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Left the group"),
            @ApiResponse(responseCode = "404", description = "Group not found",
                    content = @Content)})
    @DeleteMapping("/leave/{name}")
    void removeUserFromGroup(@PathVariable String name, @RequestAttribute("jwtToken") String token);
}
