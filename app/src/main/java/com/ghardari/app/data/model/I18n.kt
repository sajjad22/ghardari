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

        // Core 4 Cards
        "card_khata" to Trans("اڌار کاتو", "ادھار کھاتہ", "Udhaar Khata"),
        "card_khata_sub" to Trans("ڏيتي ليتي، اڌارو ۽ اڪائونٽ صاف", "لین دین، ادھار اور حساب بےباق", "Lending & Borrowing Ledger"),

        "card_monthly" to Trans("مهيني جا خرچ", "ماہانہ اخراجات", "Monthly Expenditures"),
        "card_monthly_sub" to Trans("بجلي، پاڻي، رڪشا ۽ اسڪول فيس", "بجلی، پانی، رکشہ اور اسکول فیس", "Fixed Monthly Bills & Fees"),

        "card_daily" to Trans("روزانو خرچ", "روزمرہ اخراجات", "Daily Expenses"),
        "card_daily_sub" to Trans("ماني، سبزي، بوتل ۽ روز جا خرچ", "کھانا، سبزی، بوتل اور روز کا خرچ", "Daily Expenses Log"),

        "card_ration" to Trans("راشن لسٽ", "راشن لسٹ", "Monthly Ration"),
        "card_ration_sub" to Trans("پورو مهينو سودا سلف جي فهرست", "پورے مہینے کا راشن و سودا سلف", "Monthly Grocery Checklist"),

        "card_settings" to Trans("سيٽنگ ۽ بيڪ اپ", "سیٹنگ اور بیک اپ", "Settings & Backup"),
        "card_settings_sub" to Trans("نوان ڪارڊ، ٻولي ۽ بيڪ اپ", "نئے کارڈز، زبان اور بیک اپ", "Custom Cards & Backup"),

        // Monthly Expenditures Todo & Copy
        "paid" to Trans("ادا ٿيل", "ادا شدہ", "Paid"),
        "unpaid" to Trans("باقي رهيل", "غیر ادا شدہ", "Unpaid"),
        "copy_previous_month" to Trans("گذريل مهيني مان نقل ڪريو", "پچھلے مہینے سے نقل کریں", "Copy from Previous Month"),
        "copy_confirm_msg" to Trans("ڇا توهان گذريل مهيني جا خرچ هن مهيني ۾ آڻڻ چاهيو ٿا؟", "کیا آپ پچھلے مہینے کے اخراجات اس مہینے میں لانا چاہتے ہیں؟", "Do you want to copy all expenditures from last month?"),
        "copy_success" to Trans("گذريل مهيني جا خرچ ڪاميابي سان هن مهيني ۾ شامل ٿي ويا!", "پچھلے مہینے کے اخراجات شامل ہو گئے!", "Copied previous month's expenditures successfully!"),
        "add_monthly_item" to Trans("نئون ماهوار خرچ شامل ڪريو", "نیا ماہانہ خرچ شامل کریں", "Add Monthly Expenditure"),

        // Khata Debit / Credit
        "debit" to Trans("ڏنا (Debit)", "دیے (Debit)", "Debit (We Gave)"),
        "credit" to Trans("ورتا (Credit)", "لیے (Credit)", "Credit (We Received)"),
        "cleared" to Trans("حساب صاف", "حساب بےباق", "Account Cleared"),
        "clear_account" to Trans("حساب صاف ڪريو", "حساب بےباق کریں", "Clear Account"),
        "add_person" to Trans("نئون ماڻهو شامل ڪريو", "نیا شخص شامل کریں", "Add Person"),
        "person_name" to Trans("ماڻهوءَ جو نالو", "شخص کا نام", "Person Name"),
        "we_owe" to Trans("اسان کي ڏيڻا آهن", "ہم نے دینے ہیں", "We Owe"),
        "owed_to_us" to Trans("هنن کي واپس ڏيڻا آهن", "انہوں نے دینے ہیں", "They Owe Us"),
        "share_whatsapp" to Trans("واٽس ايپ تي موڪليو", "واٹس ایپ پر شیئر کریں", "Share via WhatsApp"),

        // Daily
        "add_daily_expense" to Trans("روزانو خرچ شامل ڪريو", "روزمرہ خرچ شامل کریں", "Add Daily Expense"),
        "expense_title" to Trans("خرچ جو عنوان", "خرچ کا عنوان", "Expense Title"),

        // Ration
        "add_ration_item" to Trans("راشن شيءِ شامل ڪريو", "راشن آئٹم شامل کریں", "Add Ration Item"),
        "item_name" to Trans("شيءِ جو نالو", "آئٹم کا نام", "Item Name"),
        "quantity" to Trans("مقدار", "مقدار", "Quantity"),
        "unit" to Trans("ماپ / اڪائي", "پیمائش", "Unit"),
        "est_price" to Trans("اندازن قيمت", "تخمینہ قیمت", "Est. Price"),
        "act_price" to Trans("اصلي خرچ", "اصل خرچ", "Actual Price"),
        "bought" to Trans("ورتو", "خریدا", "Bought"),
        "to_buy" to Trans("وٺڻو آهي", "خریدنا ہے", "To Buy"),

        // Custom Cards
        "add_custom_card" to Trans("نئون اسڪرين ڪارڊ شامل ڪريو", "نیا اسکرین کارڈ شامل کریں", "Add Custom Card"),
        "card_title" to Trans("ڪارڊ جو عنوان", "کارڈ کا عنوان", "Card Title"),

        // Common
        "amount" to Trans("رقم (روپيا)", "رقم (روپے)", "Amount (Rs.)"),
        "notes" to Trans("تفصيل / نوٽ", "تفصیل / نوٹ", "Details / Note"),
        "save" to Trans("محفوظ ڪريو", "محفوظ کریں", "Save"),
        "cancel" to Trans("رد ڪريو", "منسوخ", "Cancel"),
        "delete" to Trans("ختم ڪريو", "حذف کریں", "Delete"),
        "back" to Trans("واپس", "واپس", "Back"),
        "export_backup" to Trans("ڊيٽا بيڪ اپ وٺو (JSON)", "ڈیٹا کا بیک اپ لیں (JSON)", "Export Backup (JSON)"),
        "no_data" to Trans("في الحال ڪوبه رڪارڊ ناهي", "فی الحال کوئی ریکارڈ موجود نہیں", "No records found")
    )
}
