package edu.kh.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;

@RestController // REST API 전용 컨트롤러 (@ResponseBody 생략 가능)
@RequestMapping("/api/todos") // RESTful API: /api/todos 로 통일
public class TodoApiController {

    @Autowired
    private TodoService service;

    // [1] 전체 Todo 목록 조회 (GET /api/todos)
    @GetMapping
    public List<Todo> getAllTodos() {
        return service.selectList();
    }

    // [2] 전체 Todo 개수 조회 (GET /api/todos/totalCount)
    @GetMapping("/totalCount")
    public int getTotalCount() {
        return service.getTotalCount();
    }

    // [3] 완료 Todo 개수 조회 (GET /api/todos/completeCount)
    @GetMapping("/completeCount")
    public int getCompleteCount() {
        return service.getCompleteCount();
    }

    // [4] 할 일 추가 (POST /api/todos)
    @PostMapping
    public int addTodo(@RequestBody Todo todo) {
        return service.addTodo(todo.getTodoTitle(), todo.getTodoContent());
    }

    // [5] 할 일 상세 조회 (GET /api/todos/{todoNo})
    @GetMapping("/{todoNo}")
    public Todo getTodo(@PathVariable("todoNo") int todoNo) {
        return service.todoDetail(todoNo);
    }

    // [6] 할 일 삭제 (DELETE /api/todos/{todoNo})
    @DeleteMapping("/{todoNo}")
    public int deleteTodo(@PathVariable("todoNo") int todoNo) {
        return service.todoDelete(todoNo);
    }

    // [7] 할 일 완료 여부 변경 (PUT /api/todos/{todoNo}/complete)
    @PutMapping("/{todoNo}/complete")
    public int changeComplete(@PathVariable("todoNo") int todoNo, @RequestBody Todo todo) {
        // 반드시 todoNo 를 DTO에도 반영
        Todo updateTodo = new Todo();
        updateTodo.setTodoNo(todoNo);
        updateTodo.setComplete(todo.getComplete());
        return service.changeComplete(updateTodo);
    }

    // [8] 할 일 전체 수정 (PUT /api/todos/{todoNo})
    @PutMapping("/{todoNo}")
    public int updateTodo(@PathVariable("todoNo") int todoNo, @RequestBody Todo todo) {
        todo.setTodoNo(todoNo); // PathVariable로 받은 todoNo 반영
        return service.todoUpdate(todo);
    }
}
