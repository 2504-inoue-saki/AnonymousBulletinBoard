package com.example.form.controller;

import com.example.form.controller.form.CommentForm;
import com.example.form.controller.form.ReportForm;
import com.example.form.service.ReportService;
import com.example.form.service.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class FormController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;

    // セッションからエラーメッセージを取得する
    private ModelAndView addErrorMessageFromSession(ModelAndView mav, HttpSession session) {
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            Integer sessionId = (Integer) session.getAttribute("sessionId");
            mav.addObject("errorMessage", errorMessage);
            mav.addObject("sessionId", sessionId);
            session.removeAttribute("errorMessage");
            session.removeAttribute("sessionId");
        }
        return mav;
    }

    // セッションにエラーメッセージを格納する
    private void setErrorMessageToSession(HttpSession session, BindingResult result, Integer sessionId) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                session.setAttribute("errorMessage", error.getDefaultMessage());
                break; // 最初のエラーメッセージのみ保持
            }
            if (sessionId != null) {
                session.setAttribute("sessionId", sessionId);
            }
        }
    }

    /*
     * 投稿内容表示処理
     * すべての投稿を更新日時の降順で取得して表示
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start", required = false) String start,
                            @RequestParam(name = "end", required = false) String end,
                            HttpSession session) {
        ModelAndView mav = new ModelAndView("/top");
        mav = addErrorMessageFromSession(mav, session);

        // 投稿とコメントを取得
        List<ReportForm> reportList;
        if (start != null && end != null) {
            // Date 型の引数を渡す
            reportList = reportService.findAllReport(start, end);
        } else {
            // 引数なしで全件取得
            reportList = reportService.findAllReport(null, null);
        }
        List<CommentForm> commentList = commentService.findAllComment();

        mav.addObject("commentMap", commentList.stream()
                .collect(Collectors.groupingBy(CommentForm::getReportId)));
        mav.addObject("commentFormMap", reportList.stream()
                .collect(Collectors.toMap(ReportForm::getId, report -> new CommentForm())));
        mav.addObject("contents", reportList);
        return mav;
    }

    /*
     * 新規投稿ボタン押下時の処理
     * /newに遷移
     */
    @GetMapping("/new")
    public ModelAndView newContent(HttpSession session) {
        ModelAndView mav = new ModelAndView("/new", "formModel", new ReportForm());
        mav = addErrorMessageFromSession(mav, session);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@Valid @ModelAttribute("formModel") ReportForm reportForm,
                                   BindingResult result,
                                   HttpSession session) {
        if (result.hasErrors()) {
            setErrorMessageToSession(session, result, null);
            return new ModelAndView("redirect:/new");
        }
        reportService.saveReport(reportForm);
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿編集画面への遷移処理
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id,
                                    HttpSession session) {
        ModelAndView mav = new ModelAndView("/edit", "formModel", reportService.editReport(id));
        mav = addErrorMessageFromSession(mav, session);
        return mav;
    }

    /*
     * 投稿編集画面での更新ボタン押下処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel")
                                      @Valid ReportForm report,
                                      BindingResult result,
                                      HttpSession session) {
        if (result.hasErrors()) {
            setErrorMessageToSession(session, result, id);
            return new ModelAndView("redirect:/edit/" + id);
        }
        report.setId(id);
        report.setUpdatedDate(new Date());
        reportService.saveReport(report);
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        reportService.deleteReport(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント投稿処理
     */
    @PostMapping("/comment/{reportId}")
    public ModelAndView addComment(@PathVariable Integer reportId,
                                   @Valid @ModelAttribute("commentForm") CommentForm commentForm,
                                   BindingResult result,
                                   HttpSession session) {
        if (result.hasErrors()) {
            setErrorMessageToSession(session, result, reportId);
            return new ModelAndView("redirect:/");
        }
        commentForm.setReportId(reportId);
        commentForm.setUpdatedDate(new Date());
        reportService.updateReportUpdatedDate(reportId, new Date());
        commentService.saveComment(commentForm);
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集画面遷移処理
     */
    @GetMapping("/edit/comment/{id}")
    public ModelAndView editComment(@PathVariable Integer id,
                                    HttpSession session) {
        ModelAndView mav = new ModelAndView("/editComment", "commentFormModel", commentService.editComment(id));
        mav = addErrorMessageFromSession(mav, session);
        return mav;
    }

    /*
     * コメント編集処理
     */
    @PutMapping("/update/comment/{id}")
    public ModelAndView updateComment(@PathVariable Integer id,
                                      @Valid @ModelAttribute("commentFormModel") CommentForm comment,
                                      BindingResult result,
                                      HttpSession session) {
        if (result.hasErrors()) {
            setErrorMessageToSession(session, result, null); // sessionId は不要
            return new ModelAndView("redirect:/edit/comment/" + id);
        }
        comment.setId(id);
        comment.setUpdatedDate(new Date());
        commentService.saveComment(comment);
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/comment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}
