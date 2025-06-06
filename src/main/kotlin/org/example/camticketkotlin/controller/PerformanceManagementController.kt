package org.example.camticketkotlin.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.example.camticketkotlin.common.ApiResponse as ApiWrapper
import org.example.camticketkotlin.domain.User
import org.example.camticketkotlin.dto.request.PerformancePostCreateRequest
import org.example.camticketkotlin.dto.request.PerformancePostUpdateRequest
import org.example.camticketkotlin.dto.response.PerformanceManagementOverviewResponse
import org.example.camticketkotlin.dto.response.PerformancePostDetailResponse
import org.example.camticketkotlin.service.PerformanceManagementService
import org.example.camticketkotlin.swagger.SwaggerCreatePerformancePostResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/camticket/api/performance-management")
class PerformanceManagementController(
    private val performanceManagementService: PerformanceManagementService
) {

    @Operation(
        summary = "공연 게시글 생성",
        description = "아티스트가 공연 게시글을 등록합니다. 프로필 이미지는 필수이며, 상세 이미지는 선택입니다."
    )
    @SwaggerCreatePerformancePostResponses
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPerformancePost(
        @RequestPart("request")
        @Parameter(description = "공연 정보 JSON")
        request: PerformancePostCreateRequest,

        @RequestPart("profileImage")
        @Parameter(description = "필수: 프로필 이미지", required = true)
        profileImage: MultipartFile,

        @RequestPart("detailImages", required = false)
        @Parameter(description = "선택: 상세 이미지 (0개 이상 가능)", required = false)
        detailImages: List<MultipartFile> = emptyList(),

        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiWrapper<Long>> {
        val postId =  performanceManagementService
            .createPerformancePost(request, profileImage, detailImages, user)
            .id!!

        return ResponseEntity
            .status(201)
            .body(ApiWrapper.created("공연 게시글이 등록되었습니다.", postId))
    }

    @Operation(
        summary = "공연 게시글 오버뷰 조회",
        description = "공연 관리 페이지에서 관리자가 작성한 공연들을 조회하고, 각 게시물에 대해 프로필 이미지, 식별 ID, 마지막 회차 정보를 제공합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 아티스트 ID"),
            ApiResponse(responseCode = "404", description = "등록된 공연이 없습니다.")
        ]
    )
    @GetMapping("/overview")
    fun getArtistPerformanceOverview(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiWrapper<List<PerformanceManagementOverviewResponse>>> {
        val response = performanceManagementService.getOverviewByUser(user)
        return ResponseEntity.ok(ApiWrapper.success(response))
    }

    @Operation(
        summary = "공연 게시글 상세 조회",
        description = "식별번호(postId)를 기반으로 해당 공연 게시글의 전체 정보를 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(responseCode = "404", description = "해당 게시글 없음")
        ]
    )
    @GetMapping("/{postId}")
    fun getPerformancePostById(
        @PathVariable postId: Long
    ): ResponseEntity<ApiWrapper<PerformancePostDetailResponse>> {
        val response = performanceManagementService.getPostById(postId)
        return ResponseEntity.ok(ApiWrapper.success(response))
    }

    @Operation(summary = "공연 게시글 삭제", description = "postId와 로그인된 유저 정보로 삭제 수행")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "삭제 성공"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "해당 게시글 없음")
        ]
    )
    @DeleteMapping("/{postId}")
    fun deletePerformancePost(
        @PathVariable postId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiWrapper<Unit>> {
        performanceManagementService.deletePerformancePost(postId, user)
        return ResponseEntity.ok(ApiWrapper.success("공연 게시글이 삭제되었습니다."))
    }


    @Operation(
        summary = "공연 게시글 수정",
        description = "아티스트가 기존 공연 게시글을 수정합니다. 기존 프로필 이미지는 수정할 수 없고, 상세 이미지는 최대 4장까지 추가만 가능합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 (상세 이미지 초과 등)"),
            ApiResponse(responseCode = "403", description = "권한 없음"),
            ApiResponse(responseCode = "404", description = "해당 게시글 없음")
        ]
    )
    @PutMapping(
        value = ["/{postId}"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updatePerformancePost(
        @PathVariable postId: Long,

        @RequestPart("request")
        @Parameter(description = "공연 수정 정보 JSON")
        request: @Valid PerformancePostUpdateRequest,

        @RequestPart("newDetailImages", required = false)
        @Parameter(description = "새로 추가할 상세 이미지 (기존 이미지는 삭제 불가)", required = false)
        newDetailImages: List<MultipartFile> = emptyList(),

        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiWrapper<Long>> {
        val updatedPostId = performanceManagementService
            .updatePerformancePost(postId, request, newDetailImages, user)

        return ResponseEntity.ok(ApiWrapper.success(updatedPostId, "공연 게시글이 수정되었습니다."))
    }

}




