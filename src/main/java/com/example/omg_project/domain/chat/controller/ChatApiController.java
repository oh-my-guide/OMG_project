package com.example.omg_project.domain.chat.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.omg_project.domain.chat.dto.ChatMessageDTO;
import com.example.omg_project.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 채팅 API 컨트롤러 클래스
 * RESTful API 엔드포인트를 정의하여 클라이언트가 채팅 관련 기능을 사용할 수 있도록 함
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final ChatService chatService;
    private final AmazonS3 amazonS3;

    /**
     * 특정 채팅방의 메시지를 조회하는 엔드포인트
     * HTTP GET 요청을 받아 해당 채팅방의 메시지를 조회하고, 메시지 목록을 반환
     *
     * @param roomId 조회할 채팅방의 ID
     * @return 채팅방의 메시지 목록(ChatMessageDTO 객체의 리스트)
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable("roomId") Long roomId) {
        List<ChatMessageDTO> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 파일을 S3 버킷에 업로드하는 엔드포인트
     * HTTP POST 요청을 받아 파일을 업로드하고, 업로드된 파일의 URL을 반환
     *
     * @param file 업로드할 파일 (MultipartFile 객체)
     * @return 업로드된 파일의 URL을 포함한 응답
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, null));// 파일을 S3 버킷에 업로드
            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();  // 업로드된 파일의 URL을 생성하여 반환
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
}
