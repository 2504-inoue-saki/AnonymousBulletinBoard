package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.ReportService;
import com.example.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
    public ModelAndView top() {
        ModelAndView mav = new ModelAndView();
        // 画面遷移先を指定
        mav.setViewName("/top");

        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport();

        // commentForm用の空のentityを準備
        CommentForm commentForm = new CommentForm();
        // 準備した空のFormを保管
        mav.addObject("commentForm", commentForm);
        // コメント全権取得
        List<CommentForm> commentDate = commentService.findAllComment();
        // コメントデータオブジェクトを保管
        mav.addObject("comments", commentDate);

        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        return mav;
    }

    /*
     * 新規投稿ボタン押下時の処理
     * /newに遷移
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
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
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm){
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿編集処理
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

    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel") ReportForm report) {
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
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
                                   @ModelAttribute("commentFormModel") CommentForm commentForm){
        commentForm.setReportId(reportId);
        // 投稿をテーブルに格納
        commentService.saveComment(commentForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * コメント編集処理
     */
    @GetMapping("/comment/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        CommentForm commentForm = new CommentForm();
        // 準備した空のFormを保管
        mav.addObject("commentFormModel", commentForm);

        //コメント対象の投稿を取得
        // 取得したコメント対象の投稿を保管
        ReportForm content = reportService.editReport(id);
        mav.addObject("content", content);

        // 画面遷移先を指定
        mav.setViewName("/comment");

        return mav;
    }

}
