package com.tu.ziik.lms.service.admin.course;

import com.tu.ziik.lms.model.admin.course.CourseCategory;

/**
 * Created by ahmadjawid on 12/27/16.
 */
public interface AdminCourseService {

    void saveCategory(CourseCategory courseCategory);

    boolean categoryExists(String name);

}
