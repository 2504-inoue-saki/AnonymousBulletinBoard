package com.example.form.controller;

import com.example.form.controller.form.CommentForm;
import com.example.form.controller.form.ReportForm;
import com.example.form.service.ReportService;
import com.example.form.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
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
    CommentService commentService;;

    /*
     * 投稿内容表示処理
     * すべての投稿を更新日時の降順で取得して表示
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name = "start", required = false) String start,
                            @RequestParam(name = "end", required = false) String end,
                            HttpSession session) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/top");

        // 投稿とコメントを全件取得
        List<ReportForm> reportList = reportService.findAllReport(start, end);
        List<CommentForm> commentList = commentService.findAllComment();

        Map<Integer, List<CommentForm>> commentMap = commentList.stream()
                .collect(Collectors.groupingBy(CommentForm::getReportId));
        mav.addObject("commentMap", commentMap);
        mav.addObject("comments", commentList);

        Map<Integer, CommentForm> commentFormMap = new HashMap<>();
        for (ReportForm report : reportList) {
            CommentForm form = new CommentForm();
            form.setReportId(report.getId());
            commentFormMap.put(report.getId(), form);
        }
        mav.addObject("commentFormMap", commentFormMap);

        mav.addObject("contents", reportList);
        return mav;
    }

    /*
     * 新規投稿ボタン押下時の処理
     * /newに遷移
     */
    @GetMapping("/new")
    public ModelAndView newContent(HttpSession session) {
        ModelAndView mav = new ModelAndView();

        // セッションからエラーメッセージを取得
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            mav.addObject("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm,
                                   BindingResult result, HttpSession session){
        // バリデーションの確認
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                String errorMessage = error.getDefaultMessage();
                session.setAttribute("errorMessage", errorMessage);
            }
            return new ModelAndView("redirect:/new");
        }
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿編集画面への遷移処理
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をセット
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 投稿編集画面での更新ボタン押下処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel") ReportForm report) {
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 現在時刻をentityにセット
        report.setUpdatedDate(new Date());
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }


    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id){
        reportService.deleteReport(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント投稿処理
     */
    @PostMapping("/comment/{reportId}")
    public ModelAndView addComment(@PathVariable Integer reportId,
                                   @ModelAttribute CommentForm commentForm){
        commentForm.setReportId(reportId);
        commentForm.setComment(commentForm.getComment()); // ★ 追加 (念のため)
        commentForm.setUpdatedDate(new Date());

        // ★ コメント保存後に、関連するレポートの更新日時を更新する処理を追加
        reportService.updateReportUpdatedDate(reportId, new Date());
        // 投稿をテーブルに格納
        commentService.saveComment(commentForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集画面遷移処理
     */
    @GetMapping("/edit/comment/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        CommentForm commentForm = new CommentForm();
        // 準備した空のFormを保管
        mav.addObject("commentFormModel", commentForm);

        // 画面遷移先を指定
        mav.setViewName("/edit/comment");

        return mav;
    }

    /*
     * コメント編集処理
     */
    @PutMapping("/update/comment/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel") CommentForm comment) {
        // UrlParameterのidを更新するentityにセット
        comment.setId(id);
        // 編集した投稿を更新
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/comment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id){
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}
