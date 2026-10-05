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
        "app_title" to Trans("گهرڌاري", "گھرداری", "Ghardari"),
        "app_subtitle" to Trans("گهرو خرچ، کاتو ۽ راشن لسٽ", "گھریلو اخراجات، کھاتہ اور راشن لسٹ", "Household Khata & Expenses"),
        "tab_expenses" to Trans("مهيني جا خرچ", "ماہانہ اخراجات", "Monthly Expenses"),
        "tab_khata" to Trans("اڌارو ۽ کاتو", "ادھار اور کھاتہ", "Khata & Lending"),
        "tab_ration" to Trans("راشن لسٽ", "راشن لسٹ", "Grocery / Ration"),
        "tab_reports" to Trans("رپورٽ ۽ بيڪ اپ", "رپورٹ اور بیک اپ", "Reports & Backup"),

        // Monthly overview
        "received_money" to Trans("ڪل آيل رقم", "کل وصولی", "Total Received"),
        "total_spent" to Trans("ڪل خرچ", "کل خرچ", "Total Spent"),
        "net_balance" to Trans("باقي بچت", "باقی رقم", "Remaining Balance"),
        "owed_to_us" to Trans("وٺڻا آهن", "لینے ہیں", "Receivable"),
        "we_owe" to Trans("ڏيڻا آهن", "دینے ہیں", "Payable"),

        // Quick Entry
        "quick_add" to Trans("تڪڙو اندراج", "فوری اندراج", "Quick Add"),
        "amount" to Trans("رقم (روپيا)", "رقم (روپے)", "Amount (Rs.)"),
        "category" to Trans("شعبو / ڪيٽيگري", "شعبہ / کیٹیگری", "Category"),
        "person" to Trans("ماڻهو / پارٽي", "شخص / پارٹی", "Person / Party"),
        "notes" to Trans("تفصيل / نوٽ", "تفصیل / نوٹ", "Details / Note"),
        "date" to Trans("تاريخ", "تاریخ", "Date"),
        "payment_method" to Trans("طريقو", "ادائیگی طریقہ", "Payment Method"),
        "save" to Trans("محفوظ ڪريو", "محفوظ کریں", "Save"),
        "cancel" to Trans("رد ڪريو", "منسوخ", "Cancel"),
        "delete" to Trans("ختم ڪريو", "حذف کریں", "Delete"),
        "edit" to Trans("تبديل ڪريو", "ترمیم کریں", "Edit"),

        // Types
        "inflow" to Trans("ورتا (آمدني)", "لیے (آمدنی)", "Money Received (Inflow)"),
        "outflow" to Trans("ڏنا (خرچ)", "دیے (خرچ)", "Money Spent (Expense)"),
        "khata_give" to Trans("کاتي ۾ ڏنا", "کھاتے میں دیے", "Lent / Paid to Party"),
        "khata_receive" to Trans("کاتي ۾ ورتا", "کھاتے سے لیے", "Received from Party"),

        // Khata
        "cleared" to Trans("حساب صاف", "حساب بےباق", "Account Cleared"),
        "uncleared" to Trans("باقي رهيل", "بقایا جات", "Pending"),
        "clear_account" to Trans("حساب صاف ڪريو", "حساب بےباق کریں", "Clear Account"),
        "add_person" to Trans("نئون ماڻهو شامل ڪريو", "نیا شخص شامل کریں", "Add Person"),
        "person_name" to Trans("ماڻهوءَ جو نالو (مثال: مس XYZ)", "نام (مثلاً: مس XYZ)", "Person Name (e.g. Miss XYZ)"),
        "phone" to Trans("فون نمبر (اختياري)", "فون نمبر (اختیاری)", "Phone Number (Optional)"),
        "we_owe_them" to Trans("اسان کي ڏيڻا آهن (درزي، ملازم)", "ہم نے دینے ہیں (درزی، وغیرہ)", "We owe them (Tailor, Vendor)"),
        "they_owe_us" to Trans("هنن کي واپس ڏيڻا آهن (اڌارو ڏنو)", "انہوں نے واپس کرنے ہیں (ادھار دیا)", "They owe us (Loan given)"),
        "share_whatsapp" to Trans("واٽس ايپ تي رسيد موڪليو", "واٹس ایپ پر شیئر کریں", "Share via WhatsApp"),

        // Ration
        "ration_title" to Trans("ماهوار راشن ۽ خريداري لسٽ", "ماہانہ راشن اور خریداری لسٹ", "Monthly Grocery & Ration List"),
        "add_item" to Trans("راشن ۾ شيءِ شامل ڪريو", "راشن آئٹم شامل کریں", "Add Ration Item"),
        "item_name" to Trans("شيءِ جو نالو (مثال: کنڊ، اٽو)", "چیز کا نام (مثلاً: چینی، آٹا)", "Item Name (e.g. Sugar, Flour)"),
        "quantity" to Trans("ڪٿ / مقدار", "مقدار", "Quantity"),
        "unit" to Trans("ماپ / اڪائي", "پیمائش کی اکائی", "Unit"),
        "est_price" to Trans("اندازن قيمت", "تخمینہ قیمت", "Est. Price"),
        "act_price" to Trans("اصلي خرچ", "اصل خرچ", "Actual Price"),
        "bought" to Trans("ورتو", "خریدا", "Bought"),
        "to_buy" to Trans("وٺڻو آهي", "خریدنا ہے", "To Buy"),
        "add_to_monthly_expenses" to Trans("راشن خرچ مهيني جي کاتي ۾ شامل ڪريو", "راشن کا کل خرچ ماہانہ کھاتے میں شامل کریں", "Add Grocery Total to Monthly Expenses"),
        "ration_added_success" to Trans("راشن جو ڪل خرچ مهيني جي خرچن ۾ شامل ٿي ويو!", "راشن کا کل خرچ ماہانہ اخراجات میں شامل ہو گیا!", "Grocery total added to monthly expenses!"),

        // Categories
        "add_category" to Trans("نئين ڪيٽيگري شامل ڪريو", "نئی کیٹیگری شامل کریں", "Add New Category"),
        "category_name" to Trans("ڪيٽيگريءَ جو نالو", "کیٹیگری کا نام", "Category Name"),
        "pick_icon" to Trans("نشان (آئڪن) چونڊيو", "نشان منتخب کریں", "Select Icon"),

        // Backup
        "export_backup" to Trans("ڊيٽا جو بيڪ اپ وٺو (JSON)", "ڈیٹا کا بیک اپ لیں (JSON)", "Export Backup (JSON)"),
        "import_backup" to Trans("بيڪ اپ مان بحال ڪريو", "بیک اپ سے بحال کریں", "Restore from Backup"),
        "backup_success" to Trans("بيڪ اپ فائل ڪاميابي سان تيار ٿي وئي!", "بیک اپ فائل تیار ہو گئی!", "Backup created successfully!"),
        "no_data" to Trans("في الحال ڪوبه رڪارڊ ناهي", "فی الحال کوئی ریکارڈ موجود نہیں", "No records found")
    )
}
