package com.jeffspring.store.controller;

import com.jeffspring.store.model.Task;
import com.jeffspring.store.repository.TaskRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDateTime;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository repo;

    @Autowired
    public TaskController(TaskRepository repo){
        this.repo = repo;
    }

    @ModelAttribute("priorities")
    public Task.Priority[] priorities(){
        return Task.Priority.values();
    }

    @GetMapping
    public String list(@RequestParam(required = false) Boolean completed,
                       HttpSession session,
                       Model model
                       )
    {
        String email = currentUserEmail(session);
        if(email == null){
            return "redirect:/login?neededLogin=1";
        }
        if(completed == null){
            model.addAttribute("tasks",repo.findAllByCreatedByOrderByCompletedAscDueDateAscIdAsc(email));
        }
        else{
            model.addAttribute("tasks",repo.findByCreatedByAndCompletedOrderByDueDateAscIdAsc(email,completed));
        }
        if (!model.containsAttribute("taskForm")) {
            model.addAttribute("taskForm", new Task());
        }
        model.addAttribute("filter", completed);
        return "tasks";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("taskForm") Task form,
                         BindingResult binding,
                         HttpSession session,
                         RedirectAttributes ra){
        String email = currentUserEmail(session);
        if(email == null){
            return "redirect:/login?neededLogin=1";
        }

        if(form.getDueDate() != null && form.getDueDate().isBefore(LocalDateTime.now())){
            binding.rejectValue("dueDate", "dueDate.past", "Due date must be in the future.");
        }

        if(binding.hasErrors()){
            ra.addFlashAttribute("org.springframework.validation.BindingResult.taskForm",binding);
            ra.addFlashAttribute("taskForm",form);
            ra.addFlashAttribute("msg","error");
            return "redirect:/tasks";
        }
        form.setCreatedBy(email);
        form.setCompleted(false);

        repo.save(form);
        ra.addFlashAttribute("msg","Success");
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes ra){
        String email = currentUserEmail(session);
        if(email == null){
            return "redirect:/login?neededLogin=1";
        }
        var taskOpt = repo.findByIdAndCreatedBy(id, email);
        if(taskOpt.isEmpty()){
            ra.addFlashAttribute("msg","Task not found");
            return "redirect:/tasks";
        }

        var g = taskOpt.get();
        g.setCompleted(!g.isCompleted());
        repo.save(g);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                        HttpSession session,
                        RedirectAttributes ra) {
        String email = currentUserEmail(session);
        if (email == null) {
            return "redirect:/login?neededLogin=1";
        }
        var taskOpt = repo.findByIdAndCreatedBy(id, email);
        if (taskOpt.isEmpty()) {
            ra.addFlashAttribute("msg", "Task not found");
            return "redirect:/tasks";
        }
        repo.delete(taskOpt.get());
        ra.addFlashAttribute("msg","Task deleted successfully");
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       HttpSession session,
                       RedirectAttributes ra,
                       Model model){
        String email = currentUserEmail(session);
        if (email == null) {
            return "redirect:/login?neededLogin=1";
        }
        var taskOpt = repo.findByIdAndCreatedBy(id, email);
        if (taskOpt.isEmpty()) {
            ra.addFlashAttribute("msg", "Task not found");
            return "redirect:/tasks";
        }
        model.addAttribute("task",taskOpt.get());
        return "task-edit";
    }

    // Important base Update method below
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes ra,
                         @Valid @ModelAttribute Task form,
                         BindingResult binding){
        String email = currentUserEmail(session);
        if (email == null) {
            return "redirect:/login?neededLogin=1";
        }
        if(form.getDueDate() != null && form.getDueDate().isBefore(LocalDateTime.now())){
            binding.rejectValue("dueDate", "dueDate.past", "Due date must be in the future.");
        }
        if(binding.hasErrors()){
            ra.addFlashAttribute("org.springframework.validation.BindingResult.task",binding);
            ra.addFlashAttribute("task",form);
            return "redirect:/tasks/" + id + "/edit";
        }
        var taskOpt = repo.findByIdAndCreatedBy(id, email);
        if (taskOpt.isEmpty()) {
            ra.addFlashAttribute("msg", "Task not found");
            return "redirect:/tasks";
        }
        var tf = taskOpt.get();
        tf.setTitle(form.getTitle());
        tf.setDescription(form.getDescription());
        tf.setPriority(form.getPriority());
        tf.setDueDate(form.getDueDate());

        repo.save(tf);
        ra.addFlashAttribute("msg","Task Updated");
        return "redirect:/tasks";

    }

    // Helper Method
    public String currentUserEmail(HttpSession session){
        Object u = session.getAttribute("user");
        if(u == null){
            return null;
        }
        try{
            return (String) u.getClass().getMethod("getEmail").invoke(u);
        } catch (Exception e){
            return null;
        }
    }

}
