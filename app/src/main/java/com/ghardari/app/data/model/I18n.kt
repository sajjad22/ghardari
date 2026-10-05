package com.ghardari.app.data.model

object I18n {
    fun t(key: String, lang: AppLanguage): String {
        val entry = strings[key] ?: return key
        return when (lang) {
            AppLanguage.SINDHI -> entry.sd
            AppLanguage.URDU -> entry.ur
            AppLanguage.ENGLISH -> entry.en
        }
    }

    private data class Trans(val sd: String, val ur: String, val en: String)

    private val strings = mapOf(
        "app_title" to Trans("گهرداري", "گھرداری", "Ghardari"),
        "app_subtitle" to Trans("گهرو خرچ، اڌار کاتو ۽ راشن", "گھریلو اخراجات، ادھار کھاتہ اور راشن", "Household Expenses & Khata"),

        // Core Cards
        "card_khata" to Trans("اڌار کاتو", "ادھار کھاتہ", "Udhaar Khata"),
        "card_khata_sub" to Trans("ڏيتي ليتي، بئنڪ ۽ اڌارو", "لین دین، بینک اور ادھار", "Lending & Borrowing Ledger"),

        "card_monthly" to Trans("مهيني جا خرچ", "ماہانہ اخراجات", "Monthly Expenditures"),
        "card_monthly_sub" to Trans("بجلي، پاڻي، رڪشا ۽ فيسون", "بجلی، پانی، رکشہ اور فیسیں", "Fixed Monthly Bills & Fees"),

        "card_daily" to Trans("روزانو خرچ", "روزمرہ اخراجات", "Daily Expenses"),
        "card_daily_sub" to Trans("ماني، سبزي ۽ روز جا خرچ", "کھانا، سبزی اور روز کا خرچ", "Daily Expenses Log"),

        "card_ration" to Trans("راشن لسٽ", "راشن لسٹ", "Monthly Ration"),
        "card_ration_sub" to Trans("سودا سلف لسٽ ۽ پرنٽ", "سودا سلف لسٹ و پرنٹ", "Monthly Grocery & Print"),

        "card_reports" to Trans("مالي رپورٽون", "مالی رپورٹس", "Financial Reports"),
        "card_reports_sub" to Trans("مڪمل خلاصو ۽ خرچن جو تجزيو", "مکمل تجزیہ اور خلاصہ", "Overview & Analytics"),

        "card_history" to Trans("تاريخ ۽ آرڪائيو", "تاریخ اور آرکائیو", "History & Archive"),
        "card_history_sub" to Trans("گذريل مهينن جا رڪارڊ", "پچھلے مہینوں کے ریکارڈز", "Past Months Records"),

        "card_settings" to Trans("سيٽنگ ۽ بيڪ اپ", "سیٹنگ اور بیک اپ", "Settings & Backup"),
        "card_settings_sub" to Trans("بيڪ اپ، بحالي ۽ ٻولي", "بیک اپ، بحالی اور زبان", "Backup, Restore & Language"),

        // Monthly Expenditures Todo & Copy
        "paid" to Trans("ادا ٿيل", "ادا شدہ", "Paid"),
        "unpaid" to Trans("باقي رهيل", "غیر ادا شدہ", "Unpaid"),
        "copy_previous_month" to Trans("گذريل مهيني مان نقل ڪريو", "پچھلے مہینے سے نقل کریں", "Copy from Previous Month"),
        "copy_confirm_msg" to Trans("ڇا توهان گذريل مهيني جا خرچ هن مهيني ۾ آڻڻ چاهيو ٿا؟ نوان خرچ اڻ ادا (Unpaid) ٿي ويندا.", "کیا آپ پچھلے مہینے کے اخراجات اس مہینے میں لانا چاہتے ہیں؟ نئے اخراجات غیر ادا شدہ (Unpaid) ہو جائیں گے۔", "Do you want to copy expenditures from last month? They will be marked as unpaid."),
        "copy_success" to Trans("گذريل مهيني جا خرچ ڪاميابي سان نئين سر شامل ڪيا ويا!", "پچھلے مہینے کے اخراجات شامل ہو گئے!", "Copied previous month's expenditures successfully!"),
        "add_monthly_item" to Trans("نئون ماهوار خرچ شامل ڪريو", "نیا ماہانہ خرچ شامل کریں", "Add Monthly Expenditure"),
        "edit_monthly_item" to Trans("ماهوار خرچ تبديل ڪريو", "ماہانہ خرچ تبدیل کریں", "Edit Monthly Expenditure"),

        // Khata Debit / Credit
        "debit" to Trans("ڏنا (Debit)", "دیے (Debit)", "Debit (We Gave)"),
        "credit" to Trans("ورتا (Credit)", "لیے (Credit)", "Credit (We Received)"),
        "cleared" to Trans("حساب صاف", "حساب بےباق", "Account Cleared"),
        "clear_account" to Trans("حساب صاف ڪريو", "حساب بےباق کریں", "Clear Account"),
        "add_person" to Trans("نئون ماڻهو شامل ڪريو", "نیا شخص شامل کریں", "Add Person"),
        "person_name" to Trans("ماڻهوءَ يا بئنڪ جو نالو", "نام یا بینک", "Person or Bank Name"),
        "we_owe" to Trans("اسان کي ڏيڻا آهن", "ہم نے دینے ہیں", "We Owe"),
        "owed_to_us" to Trans("هنن کي واپس ڏيڻا آهن", "انہوں نے دینے ہیں", "They Owe Us"),
        "share_whatsapp" to Trans("واٽس ايپ تي موڪليو", "واٹس ایپ پر شیئر کریں", "Share via WhatsApp"),

        // Daily
        "add_daily_expense" to Trans("روزانو خرچ شامل ڪريو", "روزمرہ خرچ شامل کریں", "Add Daily Expense"),
        "expense_title" to Trans("خرچ جو عنوان", "خرچ کا عنوان", "Expense Title"),

        // Ration & Shopkeeper Print
        "add_ration_item" to Trans("راشن شيءِ شامل ڪريو", "راشن آئٹم شامل کریں", "Add Ration Item"),
        "item_name" to Trans("شيءِ جو نالو", "آئٹم کا نام", "Item Name"),
        "company_brand" to Trans("ڪمپني / برانڊ (اختياري)", "کمپنی / برانڈ (اختیاری)", "Company / Brand (Optional)"),
        "quantity" to Trans("مقدار", "مقدار", "Quantity"),
        "unit" to Trans("ماپ / اڪائي", "پیمائش", "Unit"),
        "est_price" to Trans("اندازن قيمت", "تخمینہ قیمت", "Est. Price"),
        "act_price" to Trans("اصلي خرچ", "اصل خرچ", "Actual Price"),
        "bought" to Trans("ورتو", "خریدا", "Bought"),
        "to_buy" to Trans("وٺڻو آهي", "خریدنا ہے", "To Buy"),
        "print_shopkeeper_list" to Trans("دڪاندار لاءِ راشن لسٽ پرنٽ / شيئر", "دکاندار کے لیے راشن لسٹ پرنٹ / شیئر", "Print / Share Grocery List for Shopkeeper"),

        // Backup & Restore
        "export_backup" to Trans("ڊيٽا بيڪ اپ وٺو (JSON)", "ڈیٹا کا بیک اپ لیں (JSON)", "Export Backup (JSON)"),
        "restore_backup" to Trans("بيڪ اپ مان بحال ڪريو (Restore)", "بیک اپ سے بحال کریں (Restore)", "Restore from Backup"),
        "restore_confirm" to Trans("ڇا توهان واقعي بيڪ اپ مان ڊيٽا بحال ڪرڻ چاهيو ٿا؟", "کیا آپ بیک اپ سے ڈیٹا بحال کرنا چاہتے ہیں؟", "Do you want to restore data from this backup?"),
        "restore_success" to Trans("ڊيٽا ڪاميابي سان بحال ٿي وئي!", "ڈیٹا بحال ہو گیا!", "Data restored successfully!"),
        "restore_failed" to Trans("فائل پڙهڻ ۾ ناڪامي يا غلط فارميٽ", "فائل پڑھنے میں ناکامی", "Failed to restore backup file"),

        // Common
        "amount" to Trans("رقم (روپيا)", "رقم (روپے)", "Amount (Rs.)"),
        "notes" to Trans("تفصيل / نوٽ", "تفصیل / نوٹ", "Details / Note"),
        "save" to Trans("محفوظ ڪريو", "محفوظ کریں", "Save"),
        "cancel" to Trans("رد ڪريو", "منسوخ", "Cancel"),
        "delete" to Trans("ختم ڪريو", "حذف کریں", "Delete"),
        "edit" to Trans("تبديل ڪريو", "ترمیم کریں", "Edit"),
        "back" to Trans("واپس", "واپس", "Back"),
        "select_month" to Trans("مهينو چونڊيو", "مہینہ منتخب کریں", "Select Month"),
        "no_data" to Trans("في الحال ڪوبه رڪارڊ ناهي", "فی الحال کوئی ریکارڈ موجود نہیں", "No records found")
    )
}
