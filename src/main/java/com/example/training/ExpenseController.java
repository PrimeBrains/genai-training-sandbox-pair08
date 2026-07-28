package com.example.training;

import com.example.training.ExpenseItem.Category;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * 1明細の支給額を計算する。
     *
     * POST /api/reimburse
     * {"category": "TRANSPORT", "amount": 5000}
     * -> {"reimbursed": 3000}
     */
    @PostMapping("/reimburse")
    public ReimburseResponse reimburse(@RequestBody ReimburseRequest request) {
        validate(request);
        var item = new ExpenseItem(request.category(), request.amount());
        return new ReimburseResponse(expenseService.reimburse(item));
    }

    /**
     * 複数明細の支給額合計を計算する。
     *
     * POST /api/total
     * [{"category": "TRANSPORT", "amount": 5000}, ...]
     * -> {"total": 5500}
     */
    @PostMapping("/total")
    public TotalResponse total(@RequestBody List<ReimburseRequest> requests) {
        requests.forEach(this::validate);
        var items = requests.stream()
                .map(r -> new ExpenseItem(r.category(), r.amount()))
                .toList();
        return new TotalResponse(expenseService.total(items));
    }

    private void validate(ReimburseRequest request) {
        if (request.amount() < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + request.amount());
        }
    }

    record ReimburseRequest(Category category, int amount) {}
    record ReimburseResponse(int reimbursed) {}
    record TotalResponse(int total) {}
}
