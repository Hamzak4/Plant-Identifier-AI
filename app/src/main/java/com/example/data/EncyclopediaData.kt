package com.example.data

data class EncyclopediaPlant(
    val name: String,
    val nameUrdu: String,
    val scientificName: String,
    val family: String,
    val description: String,
    val descriptionUrdu: String,
    val benefits: String,
    val benefitsUrdu: String,
    val careGuide: String,
    val careGuideUrdu: String,
    val imageUrl: String
)

object EncyclopediaData {
    val plants = listOf(
        EncyclopediaPlant(
            name = "Neem",
            nameUrdu = "نیم",
            scientificName = "Azadirachta indica",
            family = "Meliaceae",
            description = "Neem is an exceptionally valuable medicinal evergreen tree native to the Indian subcontinent.",
            descriptionUrdu = "نیم برصغیر پاک و ہند کا ایک نہایت قیمتی سدا بہار درخت ہے جو اپنی طبی خصوصیات کے لیے مشہور ہے۔",
            benefits = "Antiseptic, purifies blood, treats skin diseases, and acts as natural pesticide.",
            benefitsUrdu = "جراثیم کش ہے، خون کو صاف کرتا ہے، جلد کے امراض کا علاج کرتا ہے اور قدرتی کیڑے مار دوا کا کام کرتا ہے۔",
            careGuide = "Requires full direct sunlight and warm soil with low to moderate water frequency.",
            careGuideUrdu = "کامل اور براہ راست دھوپ کی ضرورت ہوتی ہے اور اس کے لیے خشک سے درمیانی پانی کافی ہوتا ہے۔",
            imageUrl = "https://images.unsplash.com/photo-1598928636135-d146006ff4be?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Aloe Vera",
            nameUrdu = "کوار گندل (ایلوی ویرا)",
            scientificName = "Aloe barbadensis miller",
            family = "Asphodelaceae",
            description = "A succulent evergreen species known for its soothing gel extract stored in thick fleshy leaves.",
            descriptionUrdu = "گوشت دار پتوں والا ایک رسیلا سدا بہار پودا ہے جو کہ جیل کے اخراج کے لیے مشہور ہے۔",
            benefits = "Soothes skin burns, treats acne, promotes hair health, and assists digestive symptoms.",
            benefitsUrdu = "جلد کے جلنے کو ٹھنڈک دیتا ہے، ایکنی کا علاج کرتا ہے، بالوں کو بہتر بناتا ہے اور ہاضمے میں مدد دیتا ہے۔",
            careGuide = "Keep in bright filtered light with sparse watering; allow dry soil completely between water intervals.",
            careGuideUrdu = "چمکدار بالواسطہ دھوپ میں رکھیں اور پانی صرف اس وقت دیں جب مٹی مکمل طور پر خشک ہو۔",
            imageUrl = "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Tulsi",
            nameUrdu = "تلسی (مقدس تلسی)",
            scientificName = "Ocimum tenuiflorum",
            family = "Lamiaceae",
            description = "Holy Basil (Tulsi) is a sacred aromatic weed widely cultivated in South Asia for herbal teas.",
            descriptionUrdu = "مقدس تلسی ایک خوشبودار جڑی بوٹی ہے جو برصغیر پاک و ہند میں اپنی دواؤں کی قدر کے لیے اگائی جاتی ہے۔",
            benefits = "Relieves stress, boosts respiratory immunity, treats colds, and supports fever reduction.",
            benefitsUrdu = "دماغی تناؤ کو دور کرتی ہے، قوت مدافعت کو بڑھاتی ہے، نزلہ زکام اور بخار میں آرام دیتی ہے۔",
            careGuide = "Needs fertile moist soil, regular watering, and bright sunlight with direct air flow.",
            careGuideUrdu = "زرخیز نم مٹی، باقاعدہ پانی دینے اور بھرپور براہ راست دھوپ کی ضرورت ہوتی ہے۔",
            imageUrl = "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Peppermint",
            nameUrdu = "پودینہ",
            scientificName = "Mentha x piperita",
            family = "Lamiaceae",
            description = "A sprawling aromatic herb native to Europe and Asia, famous for its tingling minty taste.",
            descriptionUrdu = "ایک خوشبودار اور تیزی سے پھیلنے والی جڑی بوٹی ہے جو اپنے فرحت بخش ذائقے کے لیے جانی جاتی ہے۔",
            benefits = "Improves digestion, relieves tension headaches, freshens breath, and reduces nasal clog.",
            benefitsUrdu = "ہاضمے کو درست کرتا ہے، تناؤ کے سر درد میں آرام دیتا ہے اور سانسوں کو معطر کرتا ہے۔",
            careGuide = "Thrives in moist, rich compost and appreciates partial shade with standard daily watering.",
            careGuideUrdu = "نم دار مٹی میں اگتا ہے اور جزوی سائے کے ساتھ روزانہ باقاعدگی سے پانی مانگتا ہے۔",
            imageUrl = "https://images.unsplash.com/photo-1502082553048-f009c37129b9?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Rose",
            nameUrdu = "گلاب",
            scientificName = "Rosa",
            family = "Rosaceae",
            description = "A woody perennial flowering shrub famous for its highly decorative and colorful fragrant petals.",
            descriptionUrdu = "ایک خاردار سدا بہار سجاوٹی جھاڑی ہے جو اپنے خوبصورت اور انتہائی خوشبودار پھولوں کی بنا پر مقبول ہے۔",
            benefits = "Petal oils nourish skin cells, soothe eye irritation, and acts as stress relief tea.",
            benefitsUrdu = "اس کے تیل کا نچوڑ جلد کو نرم کرتا ہے، سوزش کو کم کرتا ہے اور گلاب کا عرق آنکھوں کو سکون دیتا ہے۔",
            careGuide = "Deep watering twice weekly; prune annually to clear thorns next to nodes, ensure direct solar air.",
            careGuideUrdu = "ہفتے میں دو بار گہرا پانی دیں اور خشک شاخوں کی کانٹ چھانٹ باقاعدگی سے کریں۔",
            imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Jasmine",
            nameUrdu = "چنبلی (یاسمین)",
            scientificName = "Jasminum officinale",
            family = "Oleaceae",
            description = "A climbing vine known for its intense perfume and star-like white flowers, being Pakistan's National Flower.",
            descriptionUrdu = "یہ ایک بیل نما پودا ہے جو اپنی مسحور کن خوشبو کے لیے مشہور ہے، یہ پاکستان کا قومی پھول بھی ہے۔",
            benefits = "Antiseptic, elevates mood, reduces strain, and used for organic perfume oils or tea formulas.",
            benefitsUrdu = "ذہنی دباؤ کو دور کرنے میں مددگار ہے، مزاج خوشگوار بناتا ہے اور اس کے پھولوں سے عطر بنایا جاتا ہے۔",
            careGuide = "Provide sturdy climbing trellis supports; needs moist soil and plenty of direct solar exposure.",
            careGuideUrdu = "اگنے کے لیے سہارے کی ضرورت ہوتی ہے، نم مٹی اور براہ راست دھوپ اس کے لیے موزوں ہے۔",
            imageUrl = "https://images.unsplash.com/photo-1507290439931-a861b5a38200?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Hibiscus",
            nameUrdu = "گڑھل (ہبسکس)",
            scientificName = "Hibiscus rosa-sinensis",
            family = "Malvaceae",
            description = "A showy tropical shrub bearing large funnel-shaped flowers of bright crimson red.",
            descriptionUrdu = "ایک نہایت خوبصورت سجاوٹی جھاڑی ہے جس پر بڑے کپ نما شوخ سرخ رنگ کے پھول کھلتے ہیں۔",
            benefits = "Packed with antioxidants, regulates blood pressure, and boosts hair and skin condition.",
            benefitsUrdu = "اینٹی آکسیڈنٹس سے بھرپور ہے، فشار خون کو منظم کرتا ہے اور بالوں کے گرنے کو کم کرتا ہے۔",
            careGuide = "Requires high organic humidity, daily watering during summer heat and bright direct light.",
            careGuideUrdu = "زیادہ نمی پسند کرتا ہے، روزانہ پانی کی ضرورت ہوتی ہے اور بھرپور دھوپ اس کے لیے فائدہ مند ہے۔",
            imageUrl = "https://images.unsplash.com/photo-1550950158-d0d960dff51b?q=80&w=300&auto=format&fit=crop"
        ),
        EncyclopediaPlant(
            name = "Lavender",
            nameUrdu = "لیوینڈر (پودا)",
            scientificName = "Lavandula angustifolia",
            family = "Lamiaceae",
            description = "An aromatic shrub valued globally for its violet petal clusters and soothing aromatic oils.",
            descriptionUrdu = "ایک سدا بہار سجاوٹی جڑی بوٹی ہے جو اپنے دلکش بنفشی پھولوں اور پرسکون خوشبو کے لیے جانی جاتی ہے۔",
            benefits = "Excellent insomnia relief, relaxes neural stress, cures insect bites and burns.",
            benefitsUrdu = "بے خوابی کو دور کرتا ہے، اعصابی دباؤ کم کرتا ہے اور اس کا تیل کیڑے کے کاٹنے پر آرام دیتا ہے۔",
            careGuide = "Requires highly alkaline, gravelly dry soil and maximum solar exposure with low watering frequency.",
            careGuideUrdu = "کم پانی کی ضرورت ہوتی ہے، ریتلی مٹی اور زیادہ سے زیادہ قدرتی دھوپ اس کی افزائش کے لیے ضروری ہے۔",
            imageUrl = "https://images.unsplash.com/photo-1528183429752-a97d0bf99b5a?q=80&w=300&auto=format&fit=crop"
        )
    )
}
