package com.tu.ziik.lms.controllers;

import com.tu.ziik.lms.model.lecturer.CourseContentPost;
import com.tu.ziik.lms.storage.StorageFileNotFoundException;
import com.tu.ziik.lms.storage.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }


    @RequestMapping(value = "/lecturer/course/{id}/list", method = RequestMethod.GET)
    public String listCourseContent(@PathVariable Long id, Model model){


        model.addAttribute("courseId", id);
        return "/lecturer/course-content-list";
    }

    @RequestMapping(value = "/lecturer/course/{id}/add", method = RequestMethod.GET)
    public String addCourseContent(@PathVariable Long id, Model model){

        model.addAttribute("courseId", id);
        model.addAttribute("post", new CourseContentPost());

        return "lecturer/course/add-content";


    }

    @GetMapping("/lecturer/course/post-list")
    public String listUploadedFiles() throws IOException {

        //model.addAttribute("post", new CourseContentPost());

//        model.addAttribute("files", storageService
//                .loadAll()
//                .map(path ->
//                        MvcUriComponentsBuilder
//                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
//                                .build().toString())
//                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/lecturer/course/post-list/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @PostMapping("/lecturer/course/courseContentPost")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @ModelAttribute("post") CourseContentPost courseContentPost,
                                   RedirectAttributes redirectAttributes) {

       // courseContentPost.setFilePath("course/" + courseContentPost.getType() + "/" + courseContentPost.getTitle());

        courseContentPost.setFilePath(storageService.createFilePath(file, courseContentPost));

        storageService.store(file, courseContentPost.getFilePath());
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/lecturer/course/courseContentPost-list";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
