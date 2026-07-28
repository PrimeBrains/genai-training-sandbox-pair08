package com.example.training;

import com.example.training.ExpenseItem.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "経費精算", description = "経費明細の支給額を計算する API")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "1明細の支給額を計算", description = "費目と金額を受け取り、精算ルールに基づいた支給額を返す。金額が負の場合は 400 を返す。")
    @PostMapping("/reimburse")
    public ReimburseResponse reimburse(@RequestBody ReimburseRequest request) {
        var item = new ExpenseItem(request.category(), request.amount());
        return new ReimburseResponse(expenseService.reimburse(item));
    }

    @Operation(summary = "複数明細の支給額合計を計算", description = "複数の経費明細を受け取り、それぞれの支給額の合計を返す。")
    @PostMapping("/total")
    public TotalResponse total(@RequestBody List<ReimburseRequest> requests) {
        var items = requests.stream()
                .map(r -> new ExpenseItem(r.category(), r.amount()))
                .toList();
        return new TotalResponse(expenseService.total(items));
    }

    record ReimburseRequest(Category category, int amount) {}
    record ReimburseResponse(int reimbursed) {}
    record TotalResponse(int total) {}
}
