package com.lumina.backend.config

import com.lumina.backend.model.entity.DailyWisdom
import com.lumina.backend.model.entity.Lesson
import com.lumina.backend.model.entity.LessonSlide
import com.lumina.backend.model.entity.SlideType
import com.lumina.backend.model.entity.ToneRuleItemEntity
import com.lumina.backend.model.entity.User
import com.lumina.backend.repository.DailyWisdomRepository
import com.lumina.backend.repository.LessonRepository
import com.lumina.backend.repository.UserRepository
import com.lumina.backend.model.entity.Vocabulary
import com.lumina.backend.repository.VocabularyRepository
import com.lumina.backend.model.entity.HskLevel
import com.lumina.backend.model.entity.Topic
import com.lumina.backend.repository.HskLevelRepository
import com.lumina.backend.repository.TopicRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

// Đã nạp dữ liệu ban đầu lên PostgreSQL Neon thành công.
// Tắt @Component để Spring Boot khởi động nhanh và nhẹ nhàng hơn.
// @Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val lessonRepository: LessonRepository,
    private val dailyWisdomRepository: DailyWisdomRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val hskLevelRepository: HskLevelRepository,
    private val topicRepository: TopicRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        initUsers()
        initRecommendedLessons()
        initDailyWisdoms()
        initHskLevels()
        initTopics()
        initVocabularies()
    }

    private fun initUsers() {
        if (userRepository.count() == 0L) {
            val user = User(
                email = "user@lumina.com",
                password = passwordEncoder.encode("password123"),
                fullName = "Alex Vance"
            )
            userRepository.save(user)
        }
    }

    private fun initHskLevels() {
        if (hskLevelRepository.count() == 0L) {
            val levels = listOf(
                HskLevel("all", "Tất cả cấp độ", null, 1),
                HskLevel("hsk1", "HSK 1", "180–200đ", 2),
                HskLevel("hsk2", "HSK 2", "180–200đ", 3),
                HskLevel("hsk3", "HSK 3", "240–270đ", 4),
                HskLevel("hsk4", "HSK 4", "240–280đ", 5),
                HskLevel("hsk5-6", "HSK 5–6", "250+đ", 6)
            )
            hskLevelRepository.saveAll(levels)
            println(">>> Đã khởi tạo thành công ${levels.size} cấp độ HSK vào Database!")
        }
    }

    private fun initTopics() {
        if (topicRepository.count() == 0L) {
            val topics = listOf(
                Topic("all", "Tất cả chủ đề", "🌐", 1),
                Topic("greeting", "Chào hỏi & Giao tiếp", "💬", 2),
                Topic("food", "Ăn uống & Ẩm thực", "🍜", 3),
                Topic("shopping", "Mua sắm & Trả giá", "🛍️", 4),
                Topic("travel", "Đi lại & Du lịch", "✈️", 5),
                Topic("hotel", "Khách sạn & Lưu trú", "🏨", 6),
                Topic("family", "Gia đình & Đời sống", "👨‍👩‍👧", 7),
                Topic("education", "Học tập & Giáo dục", "📚", 8),
                Topic("work", "Công việc & Công sở", "💼", 9),
                Topic("health", "Sức khỏe & Y tế", "🏥", 10),
                Topic("time", "Thời gian & Ngày tháng", "⏰", 11),
                Topic("weather", "Thời tiết & Khí hậu", "⛅", 12),
                Topic("emotion", "Cảm xúc & Tâm trạng", "😊", 13),
                Topic("direction", "Phương hướng & Vị trí", "🧭", 14),
                Topic("color", "Màu sắc & Hình dáng", "🎨", 15),
                Topic("entertainment", "Giải trí & Sở thích", "🎮", 16),
                Topic("sport", "Thể thao & Vận động", "⚽", 17),
                Topic("nature", "Động vật & Thiên nhiên", "🐱", 18),
                Topic("technology", "Công nghệ & Internet", "💻", 19),
                Topic("home", "Nhà cửa & Đồ dùng", "🏠", 20),
                Topic("fashion", "Trang phục & Thời trang", "👗", 21),
                Topic("finance", "Ngân hàng & Tài chính", "💳", 22),
                Topic("emergency", "Khẩn cấp & Cứu trợ", "🚨", 23)
            )
            topicRepository.saveAll(topics)
            println(">>> Đã khởi tạo thành công ${topics.size} chủ đề từ vựng vào Database!")
        }
    }

    private fun initRecommendedLessons() {
        lessonRepository.deleteAll()

        // ==========================================
        // NHÓM 1: NỀN TẢNG (FOUNDATION - Pinyin & Bộ thủ)
        // ==========================================

        val lesson1GrammarJson = """
        [
          {
            "structureOrder": 1,
            "title": "Quy tắc Biến điệu hai Thanh 3 (三声变调)",
            "formula": "Thanh 3 (ǎ) + Thanh 3 (ǎ) ➔ ĐỌC THÀNH: Thanh 2 (á) + Thanh 3 (ǎ)",
            "explanation": "Trong ngữ lưu tiếng Hán, khi hai âm tiết mang thanh 3 đứng liền nhau, âm tiết thứ nhất bắt buộc phải chuyển sang phát âm như thanh 2 để câu nói không bị ngắt quãng, nặng nề. Lưu ý: Phiên âm Pinyin viết trên sách vẫn giữ nguyên dấu thanh 3, nhưng miệng người nói phải phát âm thành thanh 2!",
            "examples": [
              {
                "hanzi": "你好 (Nǐ + hǎo)",
                "pinyinOriginal": "Nǐ + hǎo",
                "pinyinActual": "Phát âm thực tế: [Ní hǎo]",
                "meaning": "Xin chào (Nǐ thanh 3 chuyển thành Ní thanh 2).",
                "tip": "💡 Không nhận nhẹ âm Nǐ! Kéo ngắn đối với âm thứ hai người nghe.",
                "warning": null,
                "audioText": "你好"
              },
              {
                "hanzi": "很好 (Hěn + hǎo)",
                "pinyinOriginal": "Hěn + hǎo",
                "pinyinActual": "Phát âm thực tế: [Hén hǎo]",
                "meaning": "Rất tốt (Hěn thanh 3 chuyển thành Hén thanh 2).",
                "tip": null,
                "warning": "❌ Lỗi sai thường gặp: Hảo hảo (Đọc thành 4)\nNhiều người Việt hay nhầm thanh 3 với thanh 4, nên vuốt giọng đi lên từ cao độ 3 lên 5.",
                "audioText": "很好"
              },
              {
                "hanzi": "手表 (Shǒu + biǎo)",
                "pinyinOriginal": "Shǒu + biǎo",
                "pinyinActual": "Phát âm thực tế: [Shóu biǎo]",
                "meaning": "Đồng hồ đeo tay.",
                "tip": null,
                "warning": null,
                "audioText": "手表"
              }
            ],
            "examTip": "Trong phần thi Nghe HSK 1 & HSK 2, máy tính đọc biến âm rất nhanh. Khi nghe [Ní hǎo] hay [Xíyī], thí sinh phải nhận ra ngay đó là các chữ \"你好\" hay \"洗衣\" để chọn đáp án tranh vẽ tương ứng!"
          },
          {
            "structureOrder": 2,
            "title": "Thanh nhẹ (Khinh thanh - 轻声)",
            "formula": "Âm tiết thứ hai phát âm nhẹ, ngắn, không nhấn trọng âm.",
            "explanation": "Một số từ láy hoặc trợ từ trong tiếng Trung mất đi thanh điệu gốc và phát âm rất nhẹ nhàng, ví dụ: 妈妈 (māma - chữ ma thứ 2 đọc nhẹ), 谢谢 (xièxie).",
            "examples": [
              {
                "hanzi": "妈妈 · 妈妈",
                "pinyinOriginal": "māma",
                "pinyinActual": "māma",
                "meaning": "Mẹ (từ láy gia đình đều xuống thanh khinh thanh ở âm tiết sau).",
                "tip": null,
                "warning": null,
                "audioText": "妈妈"
              },
              {
                "hanzi": "你的书",
                "pinyinOriginal": "nǐ de shū",
                "pinyinActual": "nǐ de shū",
                "meaning": "Sách của bạn (trợ từ \"de\" phát âm nhẹ nhàng).",
                "tip": null,
                "warning": null,
                "audioText": "你的书"
              }
            ],
            "examTip": null
          }
        ]
        """.trimIndent()

        val lesson1DialoguesJson = """
        [
          {
            "speakerRole": "A",
            "speakerName": "王老师 (Thầy Vương)",
            "chinese": "你好，大卫！",
            "pinyin": "Nǐ hǎo, Dàwèi!",
            "vietnamese": "Chào David!",
            "badgeColor": "#5538EE"
          },
          {
            "speakerRole": "B",
            "speakerName": "大卫 (David)",
            "chinese": "王老师，您好！",
            "pinyin": "Wáng lǎoshī, nín hǎo!",
            "vietnamese": "Kính chào thầy Vương!",
            "badgeColor": "#059669"
          },
          {
            "speakerRole": "A",
            "speakerName": "王老师",
            "chinese": "你身体好吗？",
            "pinyin": "Nǐ shēntǐ hǎo ma?",
            "vietnamese": "Em có khỏe không?",
            "badgeColor": "#5538EE"
          },
          {
            "speakerRole": "B",
            "speakerName": "大卫",
            "chinese": "我很好，谢谢老师！",
            "pinyin": "Wǒ hěn hǎo, xièxie lǎoshī!",
            "vietnamese": "Em rất khỏe, cảm ơn thầy ạ! (Biến điệu: hěn hǎo ➔ hén hǎo)",
            "badgeColor": "#059669"
          }
        ]
        """.trimIndent()

        val lesson1CoreVocabJson = """
        [
          {
            "hanzi": "妈",
            "pinyin": "mā",
            "hanViet": "Ma",
            "meaning": "Mẹ (Thanh 1: cao, ngân vang 5–5)",
            "partOfSpeech": "Danh từ",
            "exampleHanzi": "妈妈爱我，我也爱妈妈。",
            "examplePinyin": "Māma ài wǒ, wǒ yě ài māma.",
            "exampleMeaning": "Mẹ yêu tôi, tôi cũng yêu mẹ."
          },
          {
            "hanzi": "麻",
            "pinyin": "má",
            "hanViet": "Ma",
            "meaning": "Cây gai / Tê rần (Thanh 2: đi lên dứt khoát 3–5)",
            "partOfSpeech": "Danh từ / Tính từ",
            "exampleHanzi": "我的脚麻了。",
            "examplePinyin": "Wǒ de jiǎo má le.",
            "exampleMeaning": "Chân tôi bị tê rồi."
          },
          {
            "hanzi": "马",
            "pinyin": "mǎ",
            "hanViet": "Mã",
            "meaning": "Con ngựa (Thanh 3: hạ xuống sâu rồi vòng lên 2–1–4)",
            "partOfSpeech": "Danh từ",
            "exampleHanzi": "草原上有很多白马。",
            "examplePinyin": "Cǎoyuán shang yǒu hěn duō bái mǎ.",
            "exampleMeaning": "Trên thảo nguyên có rất nhiều ngựa trắng."
          },
          {
            "hanzi": "骂",
            "pinyin": "mà",
            "hanViet": "Mạ",
            "meaning": "Mắng, chửi (Thanh 4: dứt khoát từ cao xuống thấp 5–1)",
            "partOfSpeech": "Động từ",
            "exampleHanzi": "老师没有骂他。",
            "examplePinyin": "Lǎoshī méiyǒu mà tā.",
            "exampleMeaning": "Thầy giáo không hề mắng bạn ấy."
          },
          {
            "hanzi": "好",
            "pinyin": "hǎo",
            "hanViet": "Hảo",
            "meaning": "Tốt, đẹp, hay, khỏe",
            "partOfSpeech": "Tính từ",
            "exampleHanzi": "今天天气很好。",
            "examplePinyin": "Jīntiān tiānqì hěn hǎo.",
            "exampleMeaning": "Thời tiết hôm nay rất tốt"
          },
          {
            "hanzi": "很",
            "pinyin": "hěn",
            "hanViet": "Khẩn",
            "meaning": "Rất, lắm (thường dùng trước tính từ)",
            "partOfSpeech": "Phó từ",
            "exampleHanzi": "汉语很有趣。",
            "examplePinyin": "Hànyǔ hěn yǒuqù.",
            "exampleMeaning": "Tiếng Trung rất thú vị"
          }
        ]
        """.trimIndent()

        // Bài 1: Pinyin 4 Thanh điệu
        val lesson1 = Lesson(
            id = "lesson_pinyin_1",
            category = "PHÁT ÂM PINYIN",
            categoryBgColor = "#FCE7F3",
            categoryTextColor = "#9333EA",
            level = "Cơ bản",
            pinyinHanziTitle = "四声与变调 · PINYIN",
            title = "4 Thanh điệu Pinyin & Quy tắc biến âm",
            description = "Làm chủ 4 thanh điệu cốt lõi (mā, má, mǎ, mà) và quy tắc biến điệu hai thanh 3 để phát âm chuẩn xác như người bản xứ.",
            durationMins = 5,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "FOUNDATION",
            orderIndex = 1,
            objectives = mutableListOf(
                "Phân biệt chuẩn xác cao độ 4 thanh điệu tiếng Hán (5-5, 3-5, 2-1-4, 5-1) qua sơ đồ thanh điệu.",
                "Nắm vững quy tắc biến điệu hai thanh 3 kinh điển trong giao tiếp và đề thi HSK.",
                "Luyện tập phát âm chuẩn xác các âm tiết dễ nhầm lẫn như: mā, má, mǎ, mà, nǐ hǎo...",
                "Tự tin phát âm từ vựng cơ bản mà không bị ngọng hay sai dấu tiếng Việt."
            ),
            grammarStructuresJson = lesson1GrammarJson,
            dialogueContext = "💡 Ngữ cảnh: David gặp Thầy giáo Vương ở sân trường và chào hỏi áp dụng biến điệu hai thanh 3 (很好 ➔ hén hǎo).",
            dialoguesJson = lesson1DialoguesJson,
            coreVocabulariesJson = lesson1CoreVocabJson
        )
        val slide1_1 = LessonSlide(
            lesson = lesson1, slideIndex = 1, category = "PHÁT ÂM PINYIN", cardType = "Khái niệm",
            slideType = SlideType.CONCEPT,
            subTitle = "四声与变调 (Sì shēng yǔ biàntiào)", title = "4 Thanh điệu Pinyin & Quy tắc biến âm",
            pinyinVariants = mutableListOf("mā", "má", "mǎ", "mà"),
            hanziVariants = mutableListOf("妈", "麻", "马", "骂"),
            hanViet = "Ma (Mẹ) - Ma (Gai) - Mã (Ngựa) - Mạ (Mắng)",
            meaning = "Bốn ý nghĩa hoàn toàn khác nhau chỉ nhờ thay đổi thanh điệu!",
            explanationText = "Bản đồ 4 Thanh điệu qua chữ \"ma\"",
            explanationSubtitle = "",
            explanationContent = "Tiếng Trung có 4 thanh điệu chính: Thanh 1 (mā – Mẹ: cao và bằng phẳng 5–5), Thanh 2 (má – Cây gai: lên giọng như dấu sắc 3–5), Thanh 3 (mǎ – Ngựa: xuống rồi lên 2–1–4), Thanh 4 (mà – Mắng: dứt khoát từ cao xuống thấp 5–1).",
            boxColor = "#5538EE",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "Thanh 1 (mā - Mẹ): cao và bằng phẳng (cao độ 5-5)."),
                ToneRuleItemEntity(2, "Thanh 2 (má - Cây gai): lên giọng như dấu sắc tiếng Việt (3-5)."),
                ToneRuleItemEntity(3, "Thanh 3 (mǎ - Ngựa): hạ giọng xuống sâu rồi lên (2-1-4)."),
                ToneRuleItemEntity(4, "Thanh 4 (mà - Mắng): dứt khoát rơi từ cao xuống thấp (5-1).")
            ), audioUrl = ""
        )
        val slide1_2 = LessonSlide(
            lesson = lesson1, slideIndex = 2, category = "PHÁT ÂM PINYIN", cardType = "Tương tác",
            slideType = SlideType.INTERACTIVE,
            subTitle = "四声与变调 (Sì shēng yǔ biàntiào)", title = "Quy tắc Biến điệu hai Thanh 3",
            pinyinVariants = mutableListOf("Nǐ hǎo ➔ [Ní hǎo]"),
            hanziVariants = mutableListOf("你好!"),
            hanViet = "Nhĩ hảo (Bạn tốt / Chào bạn)",
            meaning = "Chào bạn / Xin chào!",
            explanationText = "Quy tắc Biến điệu hai Thanh 3",
            explanationSubtitle = "",
            explanationContent = "Khi hai thanh 3 đi liền nhau, thanh 3 thứ nhất ĐỌC THÀNH THANH 2. Ví dụ: Chữ \"Nǐ\" (thanh 3) + \"hǎo\" (thanh 3) sẽ phát âm thành \"Ní hǎo\"!",
            boxColor = "#E06A3B",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "Thanh gốc: cả 你 (nǐ) và 好 (hǎo) ban đầu đều mang thanh 3."),
                ToneRuleItemEntity(2, "Biến điệu: khi phát âm liền mạch, 'nǐ' tự nhiên đọc thành 'ní'.")
            ), audioUrl = ""
        )
        val slide1_3 = LessonSlide(
            lesson = lesson1, slideIndex = 3, category = "PHÁT ÂM PINYIN", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ thanh điệu", title = "Kiểm tra phản xạ biến âm thanh 3",
            question = "Khi hai từ mang thanh 3 đi liền nhau (ví dụ: Nǐ hǎo), từ thứ nhất sẽ phát âm thế nào?",
            options = mutableListOf(
                "Giữ nguyên thanh 3 (Nǐ hǎo)",
                "Biến đổi thành thanh 2 (Ní hǎo)",
                "Biến đổi thành thanh 1 (Nī hǎo)",
                "Đọc nhẹ thành thanh nhẹ"
            ),
            correctAnswerIndex = 1,
            quizExplanation = "Quy tắc biến điệu hai thanh 3: Thanh 3 thứ nhất bắt buộc biến âm thành thanh 2 khi đọc liền mạch!",
            boxColor = "#5538EE"
        )
        val slide1_4 = LessonSlide(
            lesson = lesson1, slideIndex = 4, category = "PHÁT ÂM PINYIN", cardType = "Ghi nhớ",
            slideType = SlideType.TAKEAWAY,
            subTitle = "Luyện tập hàng ngày", title = "Thực hành phản xạ thanh điệu hôm nay",
            explanationText = "Bài tập thực hành tại nhà",
            explanationContent = "Hãy đứng trước gương và phát âm to 5 lần: \"Nǐ hǎo\" (đọc là Ní hǎo) và \"Hěn hǎo\" (đọc là Hén hǎo) để cơ miệng quen với nhịp điệu nhé!",
            boxColor = "#059669"
        )
        lesson1.slides.addAll(listOf(slide1_1, slide1_2, slide1_3, slide1_4))
        lessonRepository.save(lesson1)

        // Bài 2: Bộ thủ cơ bản
        val lesson2 = Lesson(
            id = "lesson_radicals_1",
            category = "BỘ THỦ CƠ BẢN",
            categoryBgColor = "#DBEAFE",
            categoryTextColor = "#2563EB",
            level = "Cơ bản",
            pinyinHanziTitle = "常用偏旁 · BỘ THỦ",
            title = "20 Bộ thủ thường gặp nhất trong HSK 1-2",
            description = "Nhận diện các bộ thủ nền tảng: bộ Nhân (亻), bộ Thủy (氵), bộ Mộc (木)... để suy đoán nghĩa từ vựng.",
            durationMins = 3,
            sparks = 25,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "FOUNDATION",
            orderIndex = 2
        )
        val slide2_1 = LessonSlide(
            lesson = lesson2, slideIndex = 1, category = "BỘ THỦ CƠ BẢN", cardType = "Bộ thủ 1",
            slideType = SlideType.CONCEPT,
            subTitle = "亻 (Nhân đứng)", title = "Bộ Nhân đứng (亻) - Con người",
            pinyinVariants = mutableListOf("tā", "nǐ", "men", "xiū"),
            hanziVariants = mutableListOf("他", "你", "们", "休"),
            hanViet = "Tha (Anh ấy) · Nhĩ (Bạn) · Môn (Chúng) · Hưu (Nghỉ)",
            meaning = "Biểu thị ý nghĩa liên quan mật thiết đến con người, danh xưng, hành vi và các mối quan hệ xã hội.",
            explanationText = "Bộ thủ xuất hiện nhiều nhất trong tiếng Trung",
            explanationSubtitle = "Đặc điểm nhận diện bộ Nhân đứng:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "Vị trí: luôn nằm ở phía bên trái của chữ Hán (thiên bàng)."),
                ToneRuleItemEntity(2, "Danh xưng: 他 (anh ấy), 你 (bạn), 们 (hậu tố chỉ số nhiều)."),
                ToneRuleItemEntity(3, "Hành vi: 休 (người đứng tựa gốc cây = nghỉ ngơi), 做 (làm việc)."),
                ToneRuleItemEntity(4, "Quy tắc viết: viết nét phẩy chéo trước, nét sổ thẳng đứng sau.")
            ), audioUrl = ""
        )
        val slide2_2 = LessonSlide(
            lesson = lesson2, slideIndex = 2, category = "BỘ THỦ CƠ BẢN", cardType = "Suy đoán nghĩa",
            slideType = SlideType.INTERACTIVE,
            subTitle = "氵 (Ba chấm thủy)", title = "Bộ Thủy (氵) - Bí quyết suy đoán nghĩa từ vựng",
            pinyinVariants = mutableListOf("hǎi", "hé", "xǐ", "kě"),
            hanziVariants = mutableListOf("海", "河", "洗", "渴"),
            hanViet = "Hải (Biển) · Hà (Sông) · Tẩy (Rửa) · Khát (Khát nước)",
            meaning = "Thấy bộ Ba chấm thủy 氵 thì 90% chữ đó gắn liền với nước, chất lỏng hoặc sông biển.",
            explanationText = "Quy tắc suy đoán nghĩa chữ Hán",
            explanationContent = "Khi gặp chữ Hán mới có bộ Ba chấm thủy (氵), bạn có thể suy đoán ngay từ đó liên quan đến nước: 河 (dòng sông), 海 (biển khơi), 洗 (rửa), 渴 (khát nước). Nắm chắc bộ thủ giúp bạn học từ vựng nhanh gấp 3 lần!",
            boxColor = "#2563EB"
        )
        val slide2_3 = LessonSlide(
            lesson = lesson2, slideIndex = 3, category = "BỘ THỦ CƠ BẢN", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ bộ thủ", title = "Nhận diện ý nghĩa qua bộ thủ",
            question = "Chữ '休' (xiū) gồm bộ Nhân đứng (亻 - người) đứng cạnh chữ Mộc (木 - cây). Theo bạn chữ này có nghĩa là gì?",
            options = mutableListOf(
                "Lao động vất vả",
                "Nghỉ ngơi (người tựa gốc cây)",
                "Trồng cây gây rừng",
                "Đi săn trong rừng"
            ),
            correctAnswerIndex = 1,
            quizExplanation = "Chữ 休 (xiū) là chữ hội ý kinh điển: người (亻) đứng tựa lưng vào thân cây (木) để nghỉ ngơi (休息 - xiūxi).",
            boxColor = "#5538EE"
        )
        val slide2_4 = LessonSlide(
            lesson = lesson2, slideIndex = 4, category = "BỘ THỦ CƠ BẢN", cardType = "Bí kíp bỏ túi",
            slideType = SlideType.TAKEAWAY,
            subTitle = "Lộ trình bộ thủ", title = "Tại sao nên học chữ Hán qua bộ thủ?",
            explanationText = "Quy tắc vàng học chữ Hán",
            explanationContent = "214 bộ thủ là 'bảng chữ cái' tạo nên chữ Hán. Hãy học theo cụm bộ thủ có tần suất xuất hiện cao nhất: Nhân (亻), Thủy (氵), Mộc (木), Khẩu (口) để nhớ mặt chữ siêu bền vững!",
            boxColor = "#059669"
        )
        lesson2.slides.addAll(listOf(slide2_1, slide2_2, slide2_3, slide2_4))
        lessonRepository.save(lesson2)

        // Bài 3: Pinyin âm đầu lưỡi và uốn lưỡi
        val lesson3 = Lesson(
            id = "lesson_pinyin_2",
            category = "PHÁT ÂM PINYIN",
            categoryBgColor = "#FCE7F3",
            categoryTextColor = "#9333EA",
            level = "Cơ bản",
            pinyinHanziTitle = "平翘舌音 · PHÁT ÂM",
            title = "Phân biệt âm đầu lưỡi (z, c, s) & uốn lưỡi (zh, ch, sh, r)",
            description = "Làm chủ vị trí đặt lưỡi để nói chuẩn giọng Bắc Kinh và không bị lẫn lộn giữa các âm tương tự nhau.",
            durationMins = 4,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "FOUNDATION",
            orderIndex = 3
        )
        val slide3_1 = LessonSlide(
            lesson = lesson3, slideIndex = 1, category = "PHÁT ÂM PINYIN", cardType = "Khái niệm",
            slideType = SlideType.CONCEPT,
            subTitle = "平舌音 vs 翘舌音", title = "Nhóm âm đầu lưỡi trước: z, c, s",
            pinyinVariants = mutableListOf("zā", "cā", "sā", "sì"),
            hanziVariants = mutableListOf("砸", "擦", "撒", "四"),
            hanViet = "Tạp (Đập) · Sát (Lau) · Tát (Rắc) · Tứ (Số 4)",
            meaning = "Đầu lưỡi duỗi thẳng chạm mặt sau răng trên, tuyệt đối không uốn lưỡi.",
            explanationText = "Khẩu hình chuẩn xác cho z, c, s",
            explanationSubtitle = "Lưu ý vị trí răng và lưỡi:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "z: tương đương 'ch' nhẹ tiếng Việt nhưng không bật hơi."),
                ToneRuleItemEntity(2, "c: đẩy luồng hơi mạnh dứt khoát qua kẽ răng (bật hơi)."),
                ToneRuleItemEntity(3, "s: luồng hơi ma sát nhẹ nhàng tương tự chữ 'x' tiếng Việt."),
                ToneRuleItemEntity(4, "Ví dụ kinh điển: 四 (sì - số 4), 昨天 (zuótiān - hôm qua).")
            ), audioUrl = ""
        )
        val slide3_2 = LessonSlide(
            lesson = lesson3, slideIndex = 2, category = "PHÁT ÂM PINYIN", cardType = "Đối chiếu",
            slideType = SlideType.INTERACTIVE,
            subTitle = "对比练习 (Duìbǐ liànxí)", title = "Đối chiếu cặp âm z/zh & c/ch",
            pinyinVariants = mutableListOf("zǎo", "zhǎo", "cǎo", "chǎo"),
            hanziVariants = mutableListOf("早", "找", "草", "炒"),
            hanViet = "Tảo (Sớm) · Trảo (Tìm) · Thảo (Cỏ) · Xao (Xào)",
            meaning = "Chỉ cần thay đổi tư thế thẳng hay uốn lưỡi, nghĩa của từ sẽ biến đổi hoàn toàn.",
            explanationText = "Phân biệt rạch ròi qua cặp từ tương đồng",
            explanationContent = "早 (zǎo - buổi sáng: thẳng lưỡi) vs 找 (zhǎo - tìm kiếm: uốn lưỡi).\n草 (cǎo - ngọn cỏ: thẳng lưỡi) vs 炒 (chǎo - xào nấu: uốn lưỡi).\nMẹo ghi nhớ: Nhìn thấy âm có chữ 'h' (zh, ch, sh) là BẮT BUỘC phải cong đầu lưỡi lên ngạc cứng!",
            boxColor = "#E06A3B"
        )
        val slide3_3 = LessonSlide(
            lesson = lesson3, slideIndex = 3, category = "PHÁT ÂM PINYIN", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Kiểm tra khẩu hình", title = "Nhận diện âm uốn lưỡi (翘舌音)",
            question = "Trong các âm Pinyin sau đây, âm nào là âm CUỐN LƯỠI (翘舌音) khi phát âm?",
            options = mutableListOf(
                "z (trong 昨天)",
                "c (trong 草)",
                "zh (trong 找)",
                "s (trong 四)"
            ),
            correctAnswerIndex = 2,
            quizExplanation = "Nhóm âm uốn lưỡi gồm: zh, ch, sh, r. Có chữ 'h' đi kèm là dấu hiệu nhận biết âm uốn lưỡi chạm ngạc cứng.",
            boxColor = "#5538EE"
        )
        val slide3_4 = LessonSlide(
            lesson = lesson3, slideIndex = 4, category = "PHÁT ÂM PINYIN", cardType = "Luyện vè",
            slideType = SlideType.TAKEAWAY,
            subTitle = "四是四，十是十", title = "Bài vè luyện giọng kinh điển: Bốn là bốn, Mười là mười",
            explanationText = "Thử thách luyện đọc nhanh",
            explanationContent = "四是四 (sì shì sì): Bốn là bốn (thẳng lưỡi).\n十是十 (shí shì shí): Mười là mười (uốn lưỡi).\n十四是十四 (shísì shì shísì): Mười bốn là mười bốn.\n四十是四十 (sìshí shì sìshí): Bốn mươi là bốn mươi.\nHãy đọc 3 lần với tốc độ tăng dần nhé!",
            boxColor = "#D97706"
        )
        lesson3.slides.addAll(listOf(slide3_1, slide3_2, slide3_3, slide3_4))
        lessonRepository.save(lesson3)

        // ==========================================
        // NHÓM 2: GIAO TIẾP (CONVERSATION - Ứng dụng thực tế)
        // ==========================================

        // Bài 4: Chào hỏi cơ bản
        val lesson4 = Lesson(
            id = "lesson_conversation_1",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#FEF3C7",
            categoryTextColor = "#D97706",
            level = "HSK 1",
            pinyinHanziTitle = "日常问候 · CHÀO HỎI",
            title = "Câu chào hỏi & Xã giao thường nhật",
            description = "Học 5 mẫu câu chào hỏi, cảm ơn và xin lỗi thông dụng nhất kèm cử chỉ văn hóa chuẩn xác.",
            durationMins = 3,
            sparks = 25,
            isCompleted = true,
            isRecommended = true,
            totalSlides = 4,
            stage = "CONVERSATION",
            orderIndex = 4
        )
        val slide4_1 = LessonSlide(
            lesson = lesson4, slideIndex = 1, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Chào hỏi",
            slideType = SlideType.CONCEPT,
            subTitle = "你好 vs 您好", title = "Chào hỏi cơ bản: Xin chào trong tiếng Trung",
            pinyinVariants = mutableListOf("nǐ", "hǎo", "nín", "hǎo"),
            hanziVariants = mutableListOf("你", "好", "您", "好"),
            hanViet = "Nhĩ (Bạn) · Hảo (Tốt) · Nẫm (Ngài) · Hảo (Tốt)",
            meaning = "Lời chào phổ biến nhất trong tiếng Trung, dùng cho mọi tình huống giao tiếp.",
            explanationText = "Phân biệt 你好 (Nǐ hǎo) và 您好 (Nín hǎo)",
            explanationSubtitle = "Quy tắc sử dụng:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "Chào bạn bè, đồng trang lứa: dùng 你好 (nǐ hǎo - đọc ní hǎo)."),
                ToneRuleItemEntity(2, "Kính trọng người lớn tuổi, cấp trên: dùng 您好 (nín hǎo)."),
                ToneRuleItemEntity(3, "Chào nhiều người: dùng 你们好 (nǐmen hǎo) hoặc 大家好 (dàjiā hǎo)."),
                ToneRuleItemEntity(4, "Ngữ điệu: vui tươi, tươi cười khi mở đầu cuộc trò chuyện.")
            ), audioUrl = ""
        )
        val slide4_2 = LessonSlide(
            lesson = lesson4, slideIndex = 2, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Cảm ơn & Đáp lại",
            slideType = SlideType.INTERACTIVE,
            subTitle = "谢谢 (Xièxie) & 不客气", title = "Cặp câu Cảm ơn & Đáp lại chuẩn lịch sự",
            pinyinVariants = mutableListOf("xiè", "xie", "bú", "kèqi"),
            hanziVariants = mutableListOf("谢", "谢", "不", "客气"),
            hanViet = "Tạ tạ (Cảm ơn) · Bất khách khí (Đừng khách sáo)",
            meaning = "Cách bày tỏ lòng biết ơn và câu trả lời lịch sự khi được người khác cảm ơn.",
            explanationText = "Quy tắc biến âm chữ '不' trong 不客气",
            explanationContent = "Cảm ơn: 谢谢 (xièxie - âm thứ 2 đọc thanh nhẹ lướt nhanh).\nĐáp lại: 不客气 (bú kèqi - Đừng khách sáo / Không có gì).\nLưu ý: Chữ 不 (gốc mang thanh 4) đứng trước chữ 'kè' (thanh 4) tự động biến điệu thành thanh 2 'bú'!",
            boxColor = "#0D9488"
        )
        val slide4_3 = LessonSlide(
            lesson = lesson4, slideIndex = 3, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Giao tiếp thực tế", title = "Ứng xử lịch sự khi nhận lời cảm ơn",
            question = "Khi bạn giúp đỡ ai đó và họ nói '谢谢' (Xièxie), câu đáp lại lịch sự và tự nhiên nhất là gì?",
            options = mutableListOf(
                "不用 (Bú yòng)",
                "不客气 (Bú kèqi)",
                "对不起 (Duìbuqǐ)",
                "没关系 (Méi guānxi)"
            ),
            correctAnswerIndex = 1,
            quizExplanation = "Khi nhận được lời cảm ơn, hãy đáp lại bằng '不客气' (Bú kèqi - Đừng khách sáo). '没关系' dùng để đáp lại lời xin lỗi 对不起.",
            boxColor = "#5538EE"
        )
        val slide4_4 = LessonSlide(
            lesson = lesson4, slideIndex = 4, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Tóm tắt bỏ túi",
            slideType = SlideType.TAKEAWAY,
            subTitle = "Bộ 4 cặp câu vàng", title = "4 Cặp câu giao tiếp sống còn mỗi ngày",
            explanationText = "Ghi nhớ trong lòng bàn tay",
            explanationContent = "1. Chào hỏi: 你好 (Nǐ hǎo) ➔ 你好!\n2. Cảm ơn: 谢谢 (Xièxie) ➔ 不客气 (Bú kèqi)!\n3. Xin lỗi: 对不起 (Duìbuqǐ) ➔ 没关系 (Méi guānxi)!\n4. Tạm biệt: 再见 (Zàijiàn) ➔ 明天见 (Míngtiān jiàn)!",
            boxColor = "#E06A3B"
        )
        lesson4.slides.addAll(listOf(slide4_1, slide4_2, slide4_3, slide4_4))
        lessonRepository.save(lesson4)

        // Bài 5: Số đếm 1-10 & Mua sắm
        val lesson5 = Lesson(
            id = "lesson_conversation_2",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#FEF3C7",
            categoryTextColor = "#D97706",
            level = "HSK 1",
            pinyinHanziTitle = "数字与购物 · SỐ ĐẾM",
            title = "Số đếm 1-10 & Mua sắm, hỏi giá tiền",
            description = "Làm chủ các con số từ 1 đến 10 và mẫu câu hỏi giá '多少钱' để tự tin mua sắm mọi nơi.",
            durationMins = 4,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "CONVERSATION",
            orderIndex = 5
        )
        val slide5_1 = LessonSlide(
            lesson = lesson5, slideIndex = 1, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Số đếm",
            slideType = SlideType.CONCEPT,
            subTitle = "数字 1-10 (Yī dào shí)", title = "Đếm số từ 1 đến 10 trong tiếng Trung",
            pinyinVariants = mutableListOf("yī", "èr", "sān", "sì"),
            hanziVariants = mutableListOf("一", "二", "三", "四"),
            hanViet = "Nhất (1) · Nhị (2) · Tam (3) · Tứ (4)",
            meaning = "Các chữ số nền tảng dùng cho số lượng, số điện thoại, giá tiền.",
            explanationText = "Đặc điểm hình họa số đếm",
            explanationSubtitle = "Quy tắc nhớ số:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "一 (yī: 1), 二 (èr: 2), 三 (sān: 3), 四 (sì: 4), 五 (wǔ: 5)."),
                ToneRuleItemEntity(2, "六 (liù: 6), 七 (qī: 7), 八 (bā: 8), 九 (jiǔ: 9), 十 (shí: 10)."),
                ToneRuleItemEntity(3, "Số 8 (八 - bā) đồng âm với Phát (fā) - biểu tượng may mắn, tài lộc."),
                ToneRuleItemEntity(4, "Số 6 (六 - liù) tượng trưng cho vạn sự thuận lợi, hanh thông.")
            ), audioUrl = ""
        )
        val slide5_2 = LessonSlide(
            lesson = lesson5, slideIndex = 2, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Mẫu câu hỏi giá",
            slideType = SlideType.INTERACTIVE,
            subTitle = "这个多少钱？", title = "Hỏi giá tiền: 这个多少钱？(Cái này bao nhiêu tiền?)",
            pinyinVariants = mutableListOf("zhè", "ge", "duō", "qián"),
            hanziVariants = mutableListOf("这", "个", "多", "钱"),
            hanViet = "Giá cá đa thiểu tiền (Cái này bao nhiêu tiền)",
            meaning = "Mẫu câu cửa miệng bắt buộc phải biết khi đi chợ, siêu thị hay quán ăn.",
            explanationText = "Cấu trúc hỏi giá tiền chuẩn xác",
            explanationContent = "Công thức: [Đồ vật] + 多少钱 (duōshǎo qián)?\nVí dụ: 这个多少钱？(Zhège duōshǎo qián? - Cái này bao nhiêu tiền?).\nĐơn vị tiền tệ: 块 (kuài - đồng/tệ trong khẩu ngữ), 元 (yuán - văn viết).\nTrả lời: 五块 (wǔ kuài - 5 tệ), 十块 (shí kuài - 10 tệ).",
            boxColor = "#0D9488"
        )
        val slide5_3 = LessonSlide(
            lesson = lesson5, slideIndex = 3, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ mua sắm", title = "Mẫu câu hỏi giá tiền chuẩn ngữ pháp",
            question = "Muốn hỏi 'Cái này bao nhiêu tiền?' trong tiếng Trung, câu nào sau đây ĐÚNG chuẩn ngữ pháp?",
            options = mutableListOf(
                "这个什么钱？(Zhège shénme qián?)",
                "这个多少钱？(Zhège duōshǎo qián?)",
                "这个怎么钱？(Zhège zěnme qián?)",
                "这个几钱？(Zhège jǐ qián?)"
            ),
            correctAnswerIndex = 1,
            quizExplanation = "Hỏi giá tiền luôn luôn dùng cụm cố định '多少钱' (duōshǎo qián). Cấu trúc: [Đồ vật] + 多少钱？",
            boxColor = "#5538EE"
        )
        val slide5_4 = LessonSlide(
            lesson = lesson5, slideIndex = 4, category = "HỘI THOẠI HÀNG NGÀY", cardType = "Mẹo mua sắm",
            slideType = SlideType.TAKEAWAY,
            subTitle = "太贵了，便宜点吧", title = "Mẹo mặc cả bản xứ thân thiện & hiệu quả",
            explanationText = "Bí kíp mua sắm thông minh",
            explanationContent = "Khi mua sắm ở chợ truyền thống Trung Quốc, hãy nhớ cụm từ thần thánh này:\n\"太贵了，便宜点吧！\" (Tài guì le, piányi diǎn ba! - Đắt quá, bớt chút đi nha!).\nThêm một nụ cười thân thiện sẽ giúp bạn mua được đồ giá tốt ngay!",
            boxColor = "#D97706"
        )
        lesson5.slides.addAll(listOf(slide5_1, slide5_2, slide5_3, slide5_4))
        lessonRepository.save(lesson5)

        // ==========================================
        // NHÓM 3: NGỮ PHÁP (GRAMMAR - Cấu trúc câu HSK 1)
        // ==========================================

        // Bài 6: Câu chữ "是"
        val lesson6 = Lesson(
            id = "lesson_grammar_shi",
            category = "NGỮ PHÁP TRỌNG ĐIỂM",
            categoryBgColor = "#E0E7FF",
            categoryTextColor = "#4338CA",
            level = "HSK 1",
            pinyinHanziTitle = "“是”字句 · NGỮ PHÁP",
            title = "Cấu trúc câu chữ '是' (Shì) — Là / Không phải là",
            description = "Nắm vững động từ phán đoán '是' (tương đương 'to be' trong tiếng Anh) để định danh sự vật, con người.",
            durationMins = 4,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "GRAMMAR",
            orderIndex = 6
        )
        val slide6_1 = LessonSlide(
            lesson = lesson6, slideIndex = 1, category = "NGỮ PHÁP HSK 1", cardType = "Khẳng định",
            slideType = SlideType.CONCEPT,
            subTitle = "A 是 B (A shì B)", title = "Câu khẳng định: Chủ ngữ + 是 + Danh từ",
            pinyinVariants = mutableListOf("wǒ", "shì", "lǎo", "shī"),
            hanziVariants = mutableListOf("我", "是", "老", "师"),
            hanViet = "Ngã (Tôi) · Thị (Là) · Lão sư (Giáo viên)",
            meaning = "Động từ phán đoán 是 (shì) biểu thị sự đồng nhất giữa chủ ngữ A và tân ngữ B.",
            explanationText = "Cấu trúc cơ bản nhất của ngữ pháp tiếng Trung",
            explanationSubtitle = "Ví dụ mẫu:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "我是老师 (Wǒ shì lǎoshī): Tôi là giáo viên."),
                ToneRuleItemEntity(2, "他是中国人 (Tā shì Zhōngguórén): Cậu ấy là người Trung Quốc."),
                ToneRuleItemEntity(3, "这是书 (Zhè shì shū): Đây là cuốn sách."),
                ToneRuleItemEntity(4, "Tuyệt đối không dùng 是 trước tính từ (Sai: 我是好, Đúng: 我很好).")
            ), audioUrl = ""
        )
        val slide6_2 = LessonSlide(
            lesson = lesson6, slideIndex = 2, category = "NGỮ PHÁP HSK 1", cardType = "Phủ định",
            slideType = SlideType.INTERACTIVE,
            subTitle = "A 不是 B (A bú shì B)", title = "Câu phủ định: Chủ ngữ + 不是 + Danh từ",
            pinyinVariants = mutableListOf("tā", "bú", "shì", "yī"),
            hanziVariants = mutableListOf("他", "不", "是", "医"),
            hanViet = "Tha (Anh ấy) · Bất thị (Không phải) · Y (Bác sĩ)",
            meaning = "Thêm phó từ 不 (bù) đặt trước 是 để biểu đạt sự phủ định 'không phải là'.",
            explanationText = "Quy tắc biến âm 'bú shì'",
            explanationContent = "Cấu trúc: Chủ ngữ + 不是 + Danh từ.\nVí dụ: 他不是医生 (Tā bú shì yīshēng - Anh ấy không phải bác sĩ).\n我不是越南人 (Wǒ bú shì Yuènánrén - Tôi không phải người Việt Nam).\nLưu ý: 不 (thanh 4) khi đứng trước 是 (thanh 4) tự động biến âm đọc thành 'bú shì'!",
            boxColor = "#E06A3B"
        )
        val slide6_3 = LessonSlide(
            lesson = lesson6, slideIndex = 3, category = "NGỮ PHÁP HSK 1", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ ngữ pháp", title = "Lỗi sai phổ biến với câu chữ '是'",
            question = "Câu nào sau đây SAI ngữ pháp nghiêm trọng trong tiếng Trung?",
            options = mutableListOf(
                "我是学生 (Wǒ shì xuésheng)",
                "他是中国人 (Tā shì Zhōngguórén)",
                "她是很好 (Tā shì hěn hǎo)",
                "这不是我的书 (Zhè bú shì wǒ de shū)"
            ),
            correctAnswerIndex = 2,
            quizExplanation = "ĐIỀU CẤM KỴ: Động từ '是' KHÔNG đi kèm trực tiếp với tính từ! Muốn khen 'Cô ấy rất tốt', chỉ nói '她很好' (Tā hěn hǎo), không dùng 是.",
            boxColor = "#5538EE"
        )
        val slide6_4 = LessonSlide(
            lesson = lesson6, slideIndex = 4, category = "NGỮ PHÁP HSK 1", cardType = "Ghi nhớ sống còn",
            slideType = SlideType.TAKEAWAY,
            subTitle = "Lưu ý HSK 1", title = "3 Quy tắc 'vàng' câu chữ 是",
            explanationText = "Bí kíp không bao giờ mất điểm",
            explanationContent = "1. Định danh: A là B ➔ A 是 B (我是老师).\n2. Phủ định: A không phải B ➔ A 不是 B (đọc bú shì).\n3. Câu hỏi: Thêm 吗 ở cuối ➔ A 是 B 吗？(你是学生吗？).\n4. KHÔNG dùng 是 trước tính từ đơn thuần (Sai: 我是高, Đúng: 我很高).",
            boxColor = "#059669"
        )
        lesson6.slides.addAll(listOf(slide6_1, slide6_2, slide6_3, slide6_4))
        lessonRepository.save(lesson6)

        // Bài 7: Câu chữ "有"
        val lesson7 = Lesson(
            id = "lesson_grammar_you",
            category = "NGỮ PHÁP TRỌNG ĐIỂM",
            categoryBgColor = "#E0E7FF",
            categoryTextColor = "#4338CA",
            level = "HSK 1",
            pinyinHanziTitle = "“有”字句 · NGỮ PHÁP",
            title = "Cấu trúc câu chữ '有' (Yǒu) — Có / Không có",
            description = "Biểu thị sự sở hữu đồ vật hoặc sự tồn tại của sự vật tại một địa điểm cụ thể.",
            durationMins = 4,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "GRAMMAR",
            orderIndex = 7
        )
        val slide7_1 = LessonSlide(
            lesson = lesson7, slideIndex = 1, category = "NGỮ PHÁP HSK 1", cardType = "Sở hữu",
            slideType = SlideType.CONCEPT,
            subTitle = "S + 有 + O (Yǒu)", title = "Biểu thị sở hữu: Ai đó có cái gì",
            pinyinVariants = mutableListOf("wǒ", "yǒu", "qián", "shū"),
            hanziVariants = mutableListOf("我", "有", "钱", "书"),
            hanViet = "Ngã (Tôi) · Hữu (Có) · Tiền (Tiền) · Thư (Sách)",
            meaning = "Động từ 有 (yǒu) tương đương với 'have' trong tiếng Anh, biểu thị quyền sở hữu.",
            explanationText = "Cấu trúc sở hữu chuẩn ngữ pháp",
            explanationSubtitle = "Ví dụ minh họa:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "我有钱 (Wǒ yǒu qián): Tôi có tiền."),
                ToneRuleItemEntity(2, "他有一本汉语书 (Tā yǒu yì běn Hànyǔ shū): Cậu ấy có một cuốn sách tiếng Trung."),
                ToneRuleItemEntity(3, "我家有四口人 (Wǒ jiā yǒu sì kǒu rén): Nhà tôi có 4 người."),
                ToneRuleItemEntity(4, "Đếm số người trong gia đình dùng lượng từ 口 (kǒu).")
            ), audioUrl = ""
        )
        val slide7_2 = LessonSlide(
            lesson = lesson7, slideIndex = 2, category = "NGỮ PHÁP HSK 1", cardType = "Mẫu câu tồn tại",
            slideType = SlideType.INTERACTIVE,
            subTitle = "Nơi chốn + 有 + Sự vật", title = "Biểu thị sự tồn tại: Ở đâu có cái gì",
            pinyinVariants = mutableListOf("zhuō", "shang", "yǒu", "shū"),
            hanziVariants = mutableListOf("桌", "上", "有", "书"),
            hanViet = "Trác thượng (Trên bàn) · Hữu (Có) · Thư (Sách)",
            meaning = "Dùng để miêu tả cảnh quan, đồ vật đang hiện diện tại một không gian xác định.",
            explanationText = "Mẫu câu vị trí tồn tại",
            explanationContent = "Cấu trúc chuẩn: [Nơi chốn] + 有 + [Người/Đồ vật].\nVí dụ: 桌子上有一本书 (Zhuōzi shang yǒu yì běn shū - Trên bàn có một cuốn sách).\n学校里有很多学生 (Trong trường có rất nhiều học sinh).\nPhủ định: [Nơi chốn] + 没有 + [Đồ vật] (桌子上没有书 - Trên bàn không có sách).",
            boxColor = "#0D9488"
        )
        val slide7_3 = LessonSlide(
            lesson = lesson7, slideIndex = 3, category = "NGỮ PHÁP HSK 1", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ ngữ pháp", title = "Phủ định của động từ '有'",
            question = "Để nói 'Tôi không có xe', câu nào sau đây ĐÚNG chuẩn ngữ pháp tiếng Trung?",
            options = mutableListOf(
                "我没有车 (Wǒ méiyǒu chē)",
                "我不有车 (Wǒ bù yǒu chē)",
                "我不车 (Wǒ bù chē)",
                "我有没车 (Wǒ yǒu méi chē)"
            ),
            correctAnswerIndex = 0,
            quizExplanation = "QUY TẮC CỐT LÕI: Tiếng Trung KHÔNG BAO GIỜ nói '不有'! Phủ định duy nhất của '有' bắt buộc phải là '没有' (méiyǒu).",
            boxColor = "#5538EE"
        )
        val slide7_4 = LessonSlide(
            lesson = lesson7, slideIndex = 4, category = "NGỮ PHÁP HSK 1", cardType = "Bỏ túi HSK 1",
            slideType = SlideType.TAKEAWAY,
            subTitle = "Lưu ý sống còn", title = "Hai quy tắc 'vàng' khi dùng động từ 有",
            explanationText = "Bí kíp ghi nhớ ngữ pháp",
            explanationContent = "1. Phủ định của 有 LUÔN LUÔN là 没有 (Tuyệt đối không dùng 不有).\n2. Khi hỏi sở hữu, chọn 1 trong 2 cách: '你有...吗？' HOẶC '你有没有...？'. Tuyệt đối không ghép cả hai thành '你有没有...吗？'.",
            boxColor = "#D97706"
        )
        lesson7.slides.addAll(listOf(slide7_1, slide7_2, slide7_3, slide7_4))
        lessonRepository.save(lesson7)

        // Bài 8: Trợ từ 吗, 呢, 吧
        val lesson8 = Lesson(
            id = "lesson_grammar_questions",
            category = "NGỮ PHÁP TRỌNG ĐIỂM",
            categoryBgColor = "#E0E7FF",
            categoryTextColor = "#4338CA",
            level = "HSK 1",
            pinyinHanziTitle = "语气助词 · TRỢ TỪ",
            title = "Bộ 3 trợ từ ngữ khí HSK 1: 吗 (Ma), 呢 (Ne), 吧 (Ba)",
            description = "Phân biệt rạch ròi ngữ cảnh sử dụng của 吗 (hỏi Có/Không), 呢 (còn... thì sao), 吧 (phỏng đoán, rủ rê).",
            durationMins = 4,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 4,
            stage = "GRAMMAR",
            orderIndex = 8
        )
        val slide8_1 = LessonSlide(
            lesson = lesson8, slideIndex = 1, category = "NGỮ PHÁP HSK 1", cardType = "Khái niệm",
            slideType = SlideType.CONCEPT,
            subTitle = "吗 (Ma) - Hỏi Yes/No", title = "Trợ từ 吗: Câu hỏi xác nhận Có/Không",
            pinyinVariants = mutableListOf("nǐ", "hǎo", "ma", "?"),
            hanziVariants = mutableListOf("你", "好", "吗", "？"),
            hanViet = "Nhĩ (Bạn) · Hảo (Khỏe) · Ma (Không)",
            meaning = "Đặt ở cuối câu khẳng định để biến thành câu hỏi nghi vấn toàn bộ.",
            explanationText = "Cách dùng cơ bản của 吗",
            explanationSubtitle = "Ví dụ thực tế:",
            toneRules = mutableListOf(
                ToneRuleItemEntity(1, "Vị trí: luôn đứng ở cuối cùng của câu."),
                ToneRuleItemEntity(2, "你好吗？(Nǐ hǎo ma? - Bạn khỏe không?)."),
                ToneRuleItemEntity(3, "你喝水吗？(Nǐ hē shuǐ ma? - Bạn uống nước không?)."),
                ToneRuleItemEntity(4, "Đọc thanh nhẹ lướt nhanh, không mang dấu thanh điệu.")
            ), audioUrl = ""
        )
        val slide8_2 = LessonSlide(
            lesson = lesson8, slideIndex = 2, category = "NGỮ PHÁP HSK 1", cardType = "Trợ từ 呢 & 吧",
            slideType = SlideType.INTERACTIVE,
            subTitle = "呢 vs 吧", title = "Trợ từ 呢 (Còn bạn thì sao?) & 吧 (Đi thôi nào!)",
            pinyinVariants = mutableListOf("wǒ", "men", "zǒu", "ba"),
            hanziVariants = mutableListOf("我", "们", "走", "吧"),
            hanViet = "Ngã môn (Chúng ta) · Tẩu (Đi) · Ba (Thôi)",
            meaning = "Biểu thị ngữ khí rủ rê, khuyên nhủ nhẹ nhàng hoặc phỏng đoán điều đã nắm chắc 80%.",
            explanationText = "So sánh 呢 và 吧",
            explanationContent = "1. Trợ từ 呢 (Ne): Hỏi tiếp nối 'Còn... thì sao?'. Ví dụ: 我很好，你呢？(Tôi rất khỏe, còn bạn?).\n2. Trợ từ 吧 (Ba): Rủ rê hoặc thương lượng. Ví dụ: 我们走吧！(Chúng mình đi thôi!). 便宜点吧！(Bớt chút đi nha!).\nCả hai đều đặt ở cuối câu và đọc thanh nhẹ.",
            boxColor = "#0D9488"
        )
        val slide8_3 = LessonSlide(
            lesson = lesson8, slideIndex = 3, category = "NGỮ PHÁP HSK 1", cardType = "Trắc nghiệm nhanh",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ trợ từ", title = "Chọn trợ từ ngữ khí chuẩn ngữ cảnh",
            question = "Người A nói: '我很好' (Tôi rất khỏe). A muốn hỏi tiếp 'Còn bạn thì sao?', A nên chọn câu nào?",
            options = mutableListOf(
                "你吗？(Nǐ ma?)",
                "你呢？(Nǐ ne?)",
                "你吧？(Nǐ ba?)",
                "你是？(Nǐ shì?)"
            ),
            correctAnswerIndex = 1,
            quizExplanation = "Trợ từ 呢 (ne) dùng để hỏi tiếp nối vấn đề vừa đề cập: '你呢？' (Còn bạn thì sao?).",
            boxColor = "#5538EE"
        )
        val slide8_4 = LessonSlide(
            lesson = lesson8, slideIndex = 4, category = "NGỮ PHÁP HSK 1", cardType = "Tổng kết",
            slideType = SlideType.TAKEAWAY,
            subTitle = "Bí kíp 3 chữ vàng", title = "Bản đồ phân biệt 吗 · 呢 · 吧 trong nháy mắt",
            explanationText = "Tóm tắt cốt lõi",
            explanationContent = "1. 吗 (Ma): Hỏi chưa biết thông tin (Yes/No) ➔ 你去吗？(Bạn đi không?).\n2. 呢 (Ne): Hỏi tiếp nối người khác ➔ 你呢？(Còn bạn?).\n3. 吧 (Ba): Rủ rê hoặc đoán chắc 80% ➔ 走吧！(Đi thôi!).\nCả 3 trợ từ đều là thanh nhẹ và luôn đứng cuối câu!",
            boxColor = "#059669"
        )
        lesson8.slides.addAll(listOf(slide8_1, slide8_2, slide8_3, slide8_4))
        lessonRepository.save(lesson8)

        // ==========================================
        // NHÓM 3: HSK 2 (TRUNG CẤP NỀN TẢNG)
        // ==========================================

        // Bài 9: Thời gian & Lên lịch hẹn
        val lesson9 = Lesson(
            id = "lesson_hsk2_time",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#FEF3C7",
            categoryTextColor = "#D97706",
            level = "HSK 2",
            pinyinHanziTitle = "时间与日程 · THỜI GIAN",
            title = "Hỏi giờ giấc, ngày tháng & Lên lịch hẹn",
            description = "Nắm chắc cách nói giờ phút, thứ ngày tháng và mẫu câu hẹn gặp '几点见面' chuẩn xác.",
            durationMins = 4,
            sparks = 30,
            isCompleted = true,
            isRecommended = true,
            totalSlides = 2,
            stage = "CONVERSATION",
            orderIndex = 9
        )
        val slide9_1 = LessonSlide(
            lesson = lesson9, slideIndex = 1, category = "GIAO TIẾP THỰC TẾ", cardType = "Giờ giấc",
            slideType = SlideType.CONCEPT,
            subTitle = "现在几点？(Xiànzài jǐ diǎn?)", title = "Hỏi và nói giờ trong tiếng Trung",
            pinyinVariants = mutableListOf("diǎn", "fēn", "bàn", "chà"),
            hanziVariants = mutableListOf("点", "分", "半", "差"),
            hanViet = "Điểm (Giờ) · Phân (Phút) · Bán (Rưỡi) · Sai (Kém)",
            meaning = "Cách nói giờ phút theo thứ tự từ lớn đến bé: Giờ + 点 + Phút + 分.",
            explanationText = "Cấu trúc thời gian chuẩn xác",
            explanationContent = "Hiện tại là mấy giờ: 现在几点？(Xiànzài jǐ diǎn?).\n8 giờ sáng: 早上八点 (Zǎoshang bā diǎn).\n8 rưỡi: 八点半 (Bā diǎn bàn).\n8 giờ kém 10: 差十分八点 (Chà shí fēn bā diǎn).",
            boxColor = "#2563EB"
        )
        val slide9_2 = LessonSlide(
            lesson = lesson9, slideIndex = 2, category = "GIAO TIẾP THỰC TẾ", cardType = "Lịch hẹn",
            slideType = SlideType.QUIZ,
            subTitle = "Hẹn giờ gặp gỡ", title = "Luyện tập hỏi giờ hẹn chuẩn xác",
            question = "Để hỏi 'Chúng ta mấy giờ gặp nhau?', câu nào sau đây chuẩn nhất?",
            options = mutableListOf("我们几点见面？(Wǒmen jǐ diǎn jiànmiàn?)", "我们什么时候好？", "我们点几去？", "我们何时看？"),
            correctAnswerIndex = 0,
            quizExplanation = "几点 (jǐ diǎn - mấy giờ) + 见面 (jiànmiàn - gặp gỡ).",
            boxColor = "#5538EE"
        )
        lesson9.slides.addAll(listOf(slide9_1, slide9_2))
        lessonRepository.save(lesson9)

        // Bài 10: Hỏi đường & Định vị
        val lesson10 = Lesson(
            id = "lesson_hsk2_direction",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#FEF3C7",
            categoryTextColor = "#D97706",
            level = "HSK 2",
            pinyinHanziTitle = "问路与方向 · HỎI ĐƯỜNG",
            title = "Hỏi đường, định vị & Chỉ dẫn phương hướng",
            description = "Học các từ chỉ phương hướng Đông Tây Nam Bắc, rẽ trái phải và mẫu câu '...怎么走？'.",
            durationMins = 5,
            sparks = 35,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "CONVERSATION",
            orderIndex = 10
        )
        val slide10_1 = LessonSlide(
            lesson = lesson10, slideIndex = 1, category = "GIAO TIẾP THỰC TẾ", cardType = "Chỉ dẫn",
            slideType = SlideType.CONCEPT,
            subTitle = "请问，去...怎么走？", title = "Mẫu câu hỏi đường kinh điển khi đi du lịch",
            pinyinVariants = mutableListOf("zuǒ", "yòu", "qián", "hòu"),
            hanziVariants = mutableListOf("左", "右", "前", "后"),
            hanViet = "Tả (Trái) · Hữu (Phải) · Tiền (Trước) · Hậu (Sau)",
            meaning = "Đi thẳng: 一直走 (Yìzhí zǒu). Rẽ trái: 往左拐 (Wǎng zuǒ guǎi). Rẽ phải: 往右拐 (Wǎng yòu guǎi).",
            explanationText = "Bí quyết hỏi đường tự tin",
            explanationContent = "Xin hỏi đến ngân hàng đi như thế nào:\n请问，去银行怎么走？(Qǐngwèn, qù yínháng zěnme zǒu?).",
            boxColor = "#0D9488"
        )
        val slide10_2 = LessonSlide(
            lesson = lesson10, slideIndex = 2, category = "GIAO TIẾP THỰC TẾ", cardType = "Trắc nghiệm",
            slideType = SlideType.QUIZ,
            subTitle = "Phương hướng", title = "Chọn cách nói 'Rẽ sang bên phải'",
            question = "'Rẽ phải' trong tiếng Trung nói thế nào?",
            options = mutableListOf("往右拐 (Wǎng yòu guǎi)", "往左拐 (Wǎng zuǒ guǎi)", "向前走 (Xiàng qián zǒu)", "一直去 (Yìzhí qù)"),
            correctAnswerIndex = 0,
            quizExplanation = "右 (yòu) là bên phải, 往 (wǎng) là hướng về, 拐 (guǎi) là rẽ/quẹo.",
            boxColor = "#5538EE"
        )
        lesson10.slides.addAll(listOf(slide10_1, slide10_2))
        lessonRepository.save(lesson10)

        // Bài 11: Trợ từ kết cấu "的"
        val lesson11 = Lesson(
            id = "lesson_hsk2_grammar_de",
            category = "NGỮ PHÁP TRỌNG ĐIỂM",
            categoryBgColor = "#E0E7FF",
            categoryTextColor = "#4338CA",
            level = "HSK 2",
            pinyinHanziTitle = "结构助词“的” · TRỢ TỪ",
            title = "Trợ từ kết cấu '的' (De) — Định ngữ và sở hữu",
            description = "Hiểu sâu bản chất trợ từ '的' để ghép tính từ, danh từ miêu tả đặc điểm chuẩn tự nhiên.",
            durationMins = 4,
            sparks = 30,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "GRAMMAR",
            orderIndex = 11
        )
        val slide11_1 = LessonSlide(
            lesson = lesson11, slideIndex = 1, category = "NGỮ PHÁP TRỌNG ĐIỂM", cardType = "Quy tắc 的",
            slideType = SlideType.CONCEPT,
            subTitle = "Định ngữ + 的 + Trung tâm ngữ", title = "Công thức nối tính từ và danh từ bằng chữ 的",
            pinyinVariants = mutableListOf("piàoliang", "de", "yīfu"),
            hanziVariants = mutableListOf("漂", "亮", "的", "衣", "服"),
            hanViet = "Phiêu lượng đích y phục (Quần áo đẹp)",
            meaning = "Từ miêu tả tính chất/sở hữu đứng trước 的, danh từ chính đứng sau 的.",
            explanationText = "Quy tắc vàng định ngữ",
            explanationContent = "Sách của tôi: 我的书 (Wǒ de shū).\nCô gái xinh đẹp: 漂亮的女孩 (Piàoliang de nǚhái).",
            boxColor = "#4338CA"
        )
        val slide11_2 = LessonSlide(
            lesson = lesson11, slideIndex = 2, category = "NGỮ PHÁP TRỌNG ĐIỂM", cardType = "Trắc nghiệm",
            slideType = SlideType.QUIZ,
            subTitle = "Phản xạ trợ từ 的", title = "Chọn trật tự từ đúng trong tiếng Trung",
            question = "Cụm 'Xe mới của anh ấy' trong tiếng Trung viết thế nào?",
            options = mutableListOf("他的新车 (Tā de xīn chē)", "新车他的", "他车新", "新的车他"),
            correctAnswerIndex = 0,
            quizExplanation = "Trật tự: Chủ sở hữu (他) + 的 + Tính từ miêu tả (新) + Danh từ chính (车).",
            boxColor = "#5538EE"
        )
        lesson11.slides.addAll(listOf(slide11_1, slide11_2))
        lessonRepository.save(lesson11)

        // ==========================================
        // NHÓM 4: HSK 3 (NÂNG CAO & GIAO TIẾP CHUYÊN SÂU)
        // ==========================================

        // Bài 12: Câu so sánh "比"
        val lesson12 = Lesson(
            id = "lesson_hsk3_compare_bi",
            category = "NGỮ PHÁP TRỌNG ĐIỂM",
            categoryBgColor = "#E0E7FF",
            categoryTextColor = "#4338CA",
            level = "HSK 3",
            pinyinHanziTitle = "“比”字句 · SO SÁNH",
            title = "Cấu trúc câu so sánh hơn với chữ '比' (Bǐ)",
            description = "Cấu trúc A 比 B + Tính từ để so sánh cao thấp, giá cả, thời tiết trong đề thi HSK 3.",
            durationMins = 5,
            sparks = 35,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "GRAMMAR",
            orderIndex = 12
        )
        val slide12_1 = LessonSlide(
            lesson = lesson12, slideIndex = 1, category = "NGỮ PHÁP TRỌNG ĐIỂM", cardType = "So sánh hơn",
            slideType = SlideType.CONCEPT,
            subTitle = "A 比 B + Tính từ", title = "Công thức so sánh cơ bản HSK 3",
            pinyinVariants = mutableListOf("tā", "bǐ", "wǒ", "gāo"),
            hanziVariants = mutableListOf("他", "比", "我", "高"),
            hanViet = "Tha (Anh ấy) · Tỉ (Hơn) · Ngã (Tôi) · Cao (Cao)",
            meaning = "Anh ấy cao hơn tôi. Chú ý: Tuyệt đối không dùng 很, 非常 sau câu so sánh 比.",
            explanationText = "Lưu ý sống còn thi HSK",
            explanationContent = "Đúng: 他比我高 (Tā bǐ wǒ gāo).\nSAI NGHIÊM TRỌNG: 他比我很高 (Cấm kỵ dùng 很 trong câu chữ 比).",
            boxColor = "#E06A3B"
        )
        val slide12_2 = LessonSlide(
            lesson = lesson12, slideIndex = 2, category = "NGỮ PHÁP TRỌNG ĐIỂM", cardType = "Luyện tập",
            slideType = SlideType.QUIZ,
            subTitle = "Trắc nghiệm ngữ pháp", title = "Tìm câu đúng chuẩn câu chữ '比'",
            question = "Câu nào sau đây ĐÚNG ngữ pháp?",
            options = mutableListOf("今天比昨天冷 (Jīntiān bǐ zuótiān lěng)", "今天比昨天很冷", "今天非常比昨天冷", "今天冷比昨天"),
            correctAnswerIndex = 0,
            quizExplanation = "Trong câu so sánh 比 không được dùng phó từ chỉ mức độ như 很, 非常 trước tính từ.",
            boxColor = "#5538EE"
        )
        lesson12.slides.addAll(listOf(slide12_1, slide12_2))
        lessonRepository.save(lesson12)

        // Bài 13: Câu chữ "把"
        val lesson13 = Lesson(
            id = "lesson_hsk3_ba_sentence",
            category = "NGỮ PHÁP TRỌNG ĐIỂM",
            categoryBgColor = "#E0E7FF",
            categoryTextColor = "#4338CA",
            level = "HSK 3",
            pinyinHanziTitle = "“把”字句 · NGỮ PHÁP",
            title = "Câu chữ '把' (Bǎ) — Xử lý và tác động đối tượng",
            description = "Chinh phục điểm ngữ pháp quan trọng nhất HSK 3: Chủ ngữ + 把 + Tân ngữ + Động từ + Kết quả.",
            durationMins = 5,
            sparks = 40,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "GRAMMAR",
            orderIndex = 13
        )
        val slide13_1 = LessonSlide(
            lesson = lesson13, slideIndex = 1, category = "NGỮ PHÁP TRỌNG ĐIỂM", cardType = "Câu chữ 把",
            slideType = SlideType.CONCEPT,
            subTitle = "S + 把 + O + V + Thành phần khác", title = "Bản chất câu chữ 把: Làm cho sự vật thay đổi trạng thái",
            pinyinVariants = mutableListOf("qǐng", "bǎ", "mén", "guān", "shang"),
            hanziVariants = mutableListOf("请", "把", "门", "关", "上"),
            hanViet = "Thỉnh (Xin) · Bả (Đem) · Môn (Cửa) · Quan (Đóng) · Thượng (Lại)",
            meaning = "Làm ơn đóng cửa lại. Chữ 把 nhấn mạnh hành động xử lý tác động lên cánh cửa.",
            explanationText = "Khi nào dùng câu chữ 把",
            explanationContent = "Dùng khi bạn muốn di chuyển, thay đổi vị trí hoặc xử lý xong một vật thể xác định.\nVí dụ: 把书给我 (Đưa sách cho tôi).\n把苹果洗干净 (Rửa sạch quả táo).",
            boxColor = "#059669"
        )
        val slide13_2 = LessonSlide(
            lesson = lesson13, slideIndex = 2, category = "NGỮ PHÁP TRỌNG ĐIỂM", cardType = "Trắc nghiệm 把",
            slideType = SlideType.QUIZ,
            subTitle = "Luyện phản xạ 把", title = "Sắp xếp câu chữ 把 chuẩn",
            question = "Câu nào sau đây ĐÚNG cấu trúc câu chữ 把?",
            options = mutableListOf("请把作业交给我 (Qǐng bǎ zuòyè jiāo gěi wǒ)", "请把交给我作业", "请我把作业交", "作业把请交我"),
            correctAnswerIndex = 0,
            quizExplanation = "Công thức: S + 把 + Tân ngữ (作业) + Động từ (交) + Kết quả (给我).",
            boxColor = "#5538EE"
        )
        lesson13.slides.addAll(listOf(slide13_1, slide13_2))
        lessonRepository.save(lesson13)

        // Bài 14: Phỏng vấn xin việc & Công sở
        val lesson14 = Lesson(
            id = "lesson_hsk3_interview",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#FEF3C7",
            categoryTextColor = "#D97706",
            level = "HSK 3",
            pinyinHanziTitle = "面试与求职 · CÔNG SỞ",
            title = "Phỏng vấn xin việc & Giới thiệu kinh nghiệm",
            description = "Mẫu câu tự tin giới thiệu bản thân, học vấn và kỹ năng chuyên môn trước nhà tuyển dụng Trung Quốc.",
            durationMins = 6,
            sparks = 40,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "CONVERSATION",
            orderIndex = 14
        )
        val slide14_1 = LessonSlide(
            lesson = lesson14, slideIndex = 1, category = "GIAO TIẾP THỰC TẾ", cardType = "Phỏng vấn",
            slideType = SlideType.CONCEPT,
            subTitle = "自我介绍 (Zìwǒ jièshào)", title = "Mở đầu buổi phỏng vấn ấn tượng",
            pinyinVariants = mutableListOf("jīngyàn", "zhuānyè", "gōngzuò"),
            hanziVariants = mutableListOf("经", "验", "专", "业", "工", "作"),
            hanViet = "Kinh nghiệm · Chuyên nghiệp · Công tác",
            meaning = "Tôi tốt nghiệp đại học chuyên ngành tiếng Trung và có 2 năm kinh nghiệm làm việc.",
            explanationText = "Mẫu câu ăn điểm",
            explanationContent = "我毕业于...大学，有两年相关工作经验 (Tôi tốt nghiệp đại học..., có 2 năm kinh nghiệm làm việc liên quan).",
            boxColor = "#2563EB"
        )
        val slide14_2 = LessonSlide(
            lesson = lesson14, slideIndex = 2, category = "GIAO TIẾP THỰC TẾ", cardType = "Trắc nghiệm",
            slideType = SlideType.QUIZ,
            subTitle = "Từ vựng công sở", title = "Từ 'Kinh nghiệm làm việc' là gì?",
            question = "'Kinh nghiệm làm việc' trong tiếng Trung là gì?",
            options = mutableListOf("工作经验 (Gōngzuò jīngyàn)", "学习经历 (Xuéxí jīnglì)", "个人兴趣 (Gèrén xìngqù)", "生活水平 (Shēnghuó shuǐpíng)"),
            correctAnswerIndex = 0,
            quizExplanation = "工作 (làm việc) + 经验 (kinh nghiệm).",
            boxColor = "#5538EE"
        )
        lesson14.slides.addAll(listOf(slide14_1, slide14_2))
        lessonRepository.save(lesson14)

        // Bài 15: Du lịch, đặt phòng & Khách sạn
        val lesson15 = Lesson(
            id = "lesson_hsk3_travel",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#FEF3C7",
            categoryTextColor = "#D97706",
            level = "HSK 3",
            pinyinHanziTitle = "旅行与住宿 · DU LỊCH",
            title = "Đặt phòng khách sạn, đổi vé tàu & Check-in",
            description = "Trang bị đầy đủ từ vựng và câu đàm thoại thiết yếu khi du lịch tự túc tại Trung Quốc.",
            durationMins = 5,
            sparks = 35,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "CONVERSATION",
            orderIndex = 15
        )
        val slide15_1 = LessonSlide(
            lesson = lesson15, slideIndex = 1, category = "GIAO TIẾP THỰC TẾ", cardType = "Khách sạn",
            slideType = SlideType.CONCEPT,
            subTitle = "我要预订一个房间", title = "Mẫu câu đặt phòng khách sạn",
            pinyinVariants = mutableListOf("yùdìng", "fángjiān", "rùzhù"),
            hanziVariants = mutableListOf("预", "订", "房", "间", "入", "住"),
            hanViet = "Dự đính (Đặt trước) · Phòng gian (Phòng) · Nhập trú (Nhận phòng)",
            meaning = "Tôi muốn đặt một phòng đơn / phòng đôi trong 3 đêm.",
            explanationText = "Đàm thoại quầy lễ tân",
            explanationContent = "Tôi đã đặt phòng trên mạng rồi:\n我在网上已经预订了房间 (Wǒ zài wǎngshang yǐjīng yùdìng le fángjiān).",
            boxColor = "#0D9488"
        )
        val slide15_2 = LessonSlide(
            lesson = lesson15, slideIndex = 2, category = "GIAO TIẾP THỰC TẾ", cardType = "Trắc nghiệm",
            slideType = SlideType.QUIZ,
            subTitle = "Du lịch Trung Quốc", title = "Từ nào có nghĩa là 'Đặt trước'?",
            question = "'Đặt trước (vé, phòng)' trong tiếng Trung là từ nào?",
            options = mutableListOf("预订 (Yùdìng)", "取消 (Qǔxiāo)", "退房 (Tuìfáng)", "入住 (Rùzhù)"),
            correctAnswerIndex = 0,
            quizExplanation = "预订 (yùdìng) nghĩa là đặt trước phòng/vé.",
            boxColor = "#5538EE"
        )
        lesson15.slides.addAll(listOf(slide15_1, slide15_2))
        lessonRepository.save(lesson15)

        // ==========================================
        // NHÓM 5: HSK 5 (Cao cấp - Thương mại & Hợp đồng)
        // ==========================================
        val lesson16 = Lesson(
            id = "lesson_hsk5_business",
            category = "GIAO TIẾP THỰC TẾ",
            categoryBgColor = "#F1F5F9",
            categoryTextColor = "#475569",
            level = "HSK 5",
            pinyinHanziTitle = "商务谈判与合同签署 (Shāngwù tánpàn)",
            title = "Đàm phán thương mại & Ký kết hợp đồng ngoại thương",
            description = "Chiết khấu giá thành (折扣), phương thức thanh toán L/C, T/T, thời hạn giao hàng và điều khoản hợp đồng song ngữ.",
            durationMins = 6,
            sparks = 35,
            isCompleted = false,
            isRecommended = true,
            totalSlides = 2,
            stage = "BUSINESS",
            orderIndex = 16
        )
        val slide16_1 = LessonSlide(
            lesson = lesson16, slideIndex = 1, category = "GIAO TIẾP THỰC TẾ", cardType = "Khái niệm",
            slideType = SlideType.CONCEPT,
            subTitle = "商务汉语 (HSK 5)", title = "Điều khoản đàm phán hợp đồng song ngữ",
            pinyinVariants = mutableListOf("zhékòu", "fùkuǎn", "jiāohuò", "hétong"),
            hanziVariants = mutableListOf("折扣", "付款", "交货", "合同"),
            hanViet = "Chiết khấu · Phó khoản · Giao hóa · Hợp đồng",
            meaning = "Nắm vững các thuật ngữ cốt lõi trong giao thương và ký kết thương mại quốc tế.",
            explanationText = "Đàm phán thương mại",
            explanationContent = "Thương lượng giá cả và chiết khấu:\n如果订购数量大，能否给予5%的折扣？(Rúguǒ dìnggòu shùliàng dà, néngfǒu jǐyǔ bǎifēnzhīwǔ de zhékòu?).",
            boxColor = "#0D9488"
        )
        val slide16_2 = LessonSlide(
            lesson = lesson16, slideIndex = 2, category = "GIAO TIẾP THỰC TẾ", cardType = "Trắc nghiệm",
            slideType = SlideType.QUIZ,
            subTitle = "Đàm phán thương mại", title = "Từ nào mang nghĩa 'Chiết khấu'?",
            question = "'Chiết khấu / Giảm giá theo phần trăm' trong thương mại tiếng Trung là từ nào?",
            options = mutableListOf("折扣 (Zhékòu)", "定金 (Dìngjīn)", "发票 (Fāpiào)", "索赔 (Suǒpéi)"),
            correctAnswerIndex = 0,
            quizExplanation = "折扣 (zhékòu) nghĩa là chiết khấu, giảm giá.",
            boxColor = "#5538EE"
        )
        lesson16.slides.addAll(listOf(slide16_1, slide16_2))
        lessonRepository.save(lesson16)

        println(">>> Đã khởi tạo thành công 16 bài học chuẩn lộ trình HSK phân cấp (Cơ bản, HSK 1, HSK 2, HSK 3, HSK 5)!")
    }

    private fun initDailyWisdoms() {
        dailyWisdomRepository.deleteAll()

        val wisdoms = listOf(
            DailyWisdom(
                chinese = "千里之行，始于足下",
                pinyin = "Qiān lǐ zhī xíng, shǐ yú zú xià",
                vietnamese = "Hành trình vạn dặm bắt đầu từ một bước chân.",
                meaning = "Mọi thành tựu vĩ đại trong đời đều bắt đầu từ những hành động nhỏ bé và kiên trì nhất.",
                author = "Lão Tử • 老子"
            ),
            DailyWisdom(
                chinese = "学如逆水行舟，不进则退",
                pinyin = "Xué rú nì shuǐ xíng zhōu, bù jìn zé tuì",
                vietnamese = "Học tập như thuyền bơi ngược dòng nước, không tiến ắt phải lùi.",
                meaning = "Con đường học vấn đòi hỏi sự nỗ lực và trau dồi không ngừng nghỉ mỗi ngày.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "熟能生巧",
                pinyin = "Shú néng shēng qiǎo",
                vietnamese = "Trăm hay không bằng tay quen.",
                meaning = "Chăm chỉ luyện tập hàng ngày sẽ rèn giũa phản xạ và kỹ năng đạt mức thuần thục tự nhiên.",
                author = "Thành ngữ tiếng Trung"
            ),
            DailyWisdom(
                chinese = "只要功夫深，铁杵磨成针",
                pinyin = "Zhǐyào gōngfu shēn, tiěchǔ mó chéng zhēn",
                vietnamese = "Có công mài sắt, có ngày nên kim.",
                meaning = "Chỉ cần có ý chí và lòng kiên định, việc khó đến đâu cũng có thể hoàn thành xuất sắc.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "温故而知新",
                pinyin = "Wēn gù ér zhī xīn",
                vietnamese = "Ôn cũ biết mới.",
                meaning = "Thường xuyên ôn luyện kiến thức cũ sẽ giúp bạn lĩnh hội và ngộ ra những điều mới sâu sắc hơn.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            ),
            DailyWisdom(
                chinese = "一寸光阴一寸金，寸金难买寸光阴",
                pinyin = "Yí cùn guāngyīn yí cùn jīn, cùn jīn nán mǎi cùn guāngyīn",
                vietnamese = "Một tấc thời gian một tấc vàng, tấc vàng khó mua tấc thời gian.",
                meaning = "Thời gian là tài sản quý báu nhất, hãy trân trọng từng phút giây kiên trì học tập.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "饮水思源",
                pinyin = "Yǐn shuǐ sī yuán",
                vietnamese = "Uống nước nhớ nguồn.",
                meaning = "Nhắc nhở con người luôn biết ơn và trân trọng những người đã dạy dỗ, giúp đỡ mình trưởng thành.",
                author = "Thành ngữ tiếng Trung"
            ),
            DailyWisdom(
                chinese = "活到老，学到老",
                pinyin = "Huó dào lǎo, xué dào lǎo",
                vietnamese = "Sống đến già, học đến già.",
                meaning = "Biển học vô bờ, tinh thần học hỏi là hành trình bền bỉ suốt cả cuộc đời.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "知己知彼，百战百胜",
                pinyin = "Zhī jǐ zhī bǐ, bǎi zhàn bǎi shèng",
                vietnamese = "Biết mình biết người, trăm trận trăm thắng.",
                meaning = "Nắm rõ điểm mạnh, điểm yếu của bản thân và mục tiêu là chìa khóa để chinh phục mọi kỳ thi.",
                author = "Tôn Tử Binh Pháp • 孙子兵法"
            ),
            DailyWisdom(
                chinese = "读万卷书，行万里路",
                pinyin = "Dú wàn juàn shū, xíng wàn lǐ lù",
                vietnamese = "Đọc vạn cuốn sách, đi vạn dặm đường.",
                meaning = "Kiến thức lý thuyết trong sách vở cần kết hợp với thực hành và trải nghiệm thực tế.",
                author = "Đổng Kỳ Xương • 董其昌"
            ),
            DailyWisdom(
                chinese = "锲而不舍，金石可镂",
                pinyin = "Qiè ér bù shě, jīn shí kě lòu",
                vietnamese = "Đẽo gọt không ngừng, sắt đá cũng mòn.",
                meaning = "Chỉ cần kiên trì không buông bỏ, những mục tiêu gian nan nhất cũng có thể khắc ghi thành công.",
                author = "Tuân Tử • 荀子 (Khuyến Học)"
            ),
            DailyWisdom(
                chinese = "三人行，必有我师焉",
                pinyin = "Sān rén xíng, bì yǒu wǒ shī yān",
                vietnamese = "Ba người cùng đi, ắt có người là thầy của ta.",
                meaning = "Giữ tâm thế khiêm nhường học hỏi những ưu điểm từ bất kỳ ai ta gặp trong đời.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            ),
            DailyWisdom(
                chinese = "不鸣则已，一鸣惊人",
                pinyin = "Bù míng zé yǐ, yì míng jīng rén",
                vietnamese = "Không kêu thì thôi, đã kêu ắt làm kinh động lòng người.",
                meaning = "Âm thầm tích lũy nội lực để khi thời cơ đến sẽ bứt phá ngoạn mục.",
                author = "Sử Ký Tư Mã Thiên • 史记"
            ),
            DailyWisdom(
                chinese = "种瓜得瓜，种豆得豆",
                pinyin = "Zhòng guā dé guā, zhòng dòu dé dòu",
                vietnamese = "Gieo nhân nào gặt quả nấy (Trồng dưa được dưa, trồng đậu được đậu).",
                meaning = "Nỗ lực học tập chăm chỉ hôm nay chắc chắn sẽ đem lại trái ngọt rực rỡ ngày mai.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "机不可失，时不再来",
                pinyin = "Jī bù kě shī, shí bú zài lái",
                vietnamese = "Cơ hội chớ để lỡ, thời khắc qua không trở lại.",
                meaning = "Hãy nắm bắt cơ hội học hỏi ngay bây giờ, đừng trì hoãn đến ngày mai.",
                author = "Ngũ Đại Sử • 五代史"
            ),
            DailyWisdom(
                chinese = "海纳百川，有容乃大",
                pinyin = "Hǎi nà bǎi chuān, yǒu róng nǎi dà",
                vietnamese = "Biển dung nạp trăm sông nên mới thành mênh mông vĩ đại.",
                meaning = "Lòng dạ bao dung rộng mở mới có thể chứa đựng tri thức và làm nên sự nghiệp lớn.",
                author = "Lâm Tắc Từ • 林则徐"
            ),
            DailyWisdom(
                chinese = "水滴石穿",
                pinyin = "Shuǐ dī shí chuān",
                vietnamese = "Nước chảy đá mòn.",
                meaning = "Mỗi ngày tích lũy một chút kiến thức, kiên trì lâu dài sẽ tạo nên kỳ tích phi thường.",
                author = "Hán Thư • 汉书"
            ),
            DailyWisdom(
                chinese = "风雨同舟",
                pinyin = "Fēng yǔ tóng zhōu",
                vietnamese = "Cùng chung một con thuyền vượt qua mưa gió bão bùng.",
                meaning = "Sự đồng lòng và đồng hành giúp con người vượt qua mọi nghịch cảnh thử thách.",
                author = "Tôn Tử • 孙子"
            ),
            DailyWisdom(
                chinese = "精益求精",
                pinyin = "Jīng yì qiú jīng",
                vietnamese = "Đã tinh xảo lại càng muốn tinh xảo hơn.",
                meaning = "Tinh thần cầu thị, không ngừng cải tiến và trau chuốt phát âm, chữ viết mỗi ngày.",
                author = "Luận Ngữ • 论语"
            ),
            DailyWisdom(
                chinese = "循序渐进",
                pinyin = "Xún xù jiàn jìn",
                vietnamese = "Theo thứ tự từng bước tiến lên.",
                meaning = "Học ngôn ngữ cần có lộ trình bài bản từ Pinyin, chữ Hán đến câu hoàn chỉnh, chớ vội vàng đốt cháy giai đoạn.",
                author = "Chu Hi • 朱熹"
            ),
            DailyWisdom(
                chinese = "日积月累",
                pinyin = "Rì jī yuè lěi",
                vietnamese = "Ngày tích tháng dồn, tích tiểu thành đại.",
                meaning = "Mỗi ngày học 5 từ vựng, sau một năm bạn đã sở hữu vốn từ vựng khổng lồ.",
                author = "Tống Sử • 宋史"
            ),
            DailyWisdom(
                chinese = "青出于蓝而胜于蓝",
                pinyin = "Qīng chū yú lán ér shèng yú lán",
                vietnamese = "Màu xanh chiết xuất từ cây chàm nhưng thắm hơn cây chàm.",
                meaning = "Hậu sinh khả úy, học trò hoàn toàn có thể vượt qua thầy nhờ sự chuyên tâm học hỏi.",
                author = "Tuân Tử • 荀子 (Khuyến Học)"
            ),
            DailyWisdom(
                chinese = "欲穷千里目，更上一层楼",
                pinyin = "Yù qióng qiān lǐ mù, gèng shàng yì céng lóu",
                vietnamese = "Muốn phóng tầm mắt nhìn ngàn dặm, hãy bước lên thêm một tầng lầu.",
                meaning = "Muốn đạt tới tầm nhìn và sự hiểu biết sâu rộng hơn, bạn cần không ngừng nâng cấp bản thân.",
                author = "Vương Chi Hoán • 王之涣"
            ),
            DailyWisdom(
                chinese = "塞翁失马，焉知非福",
                pinyin = "Sài wēng shī mǎ, yān zhī fēi fú",
                vietnamese = "Tái ông mất ngựa, trong cái rủi có cái may.",
                meaning = "Khó khăn trước mắt có thể là cơ hội để tôi luyện bản lĩnh vững vàng hơn.",
                author = "Hoài Nam Tử • 淮南子"
            ),
            DailyWisdom(
                chinese = "持之以恒",
                pinyin = "Chí zhī yǐ héng",
                vietnamese = "Giữ vững sự kiên trì tới cùng.",
                meaning = "Không bỏ cuộc giữa chừng chính là tố chất quyết định người thành công trong việc học ngoại ngữ.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "金无足赤，人无完人",
                pinyin = "Jīn wú zú chì, rén wú wán rén",
                vietnamese = "Vàng không thuần khiết tuyệt đối, người không ai thập toàn thập mỹ.",
                meaning = "Đừng sợ phát âm sai hay mắc lỗi khi mới học, dám nói và sửa sai là cách nhanh nhất để tiến bộ.",
                author = "Tục ngữ Trung Hoa"
            ),
            DailyWisdom(
                chinese = "千里之堤，溃于蚁穴",
                pinyin = "Qiān lǐ zhī dī, kuì yú yǐ xué",
                vietnamese = "Đê dài ngàn dặm sụp đổ vì một tổ kiến nhỏ.",
                meaning = "Hãy cẩn trọng rèn chuẩn phát âm thanh điệu ngay từ ngày đầu tiên để tránh lỗi sai khó sửa về sau.",
                author = "Hàn Phi Tử • 韩非子"
            ),
            DailyWisdom(
                chinese = "有志者，事竟成",
                pinyin = "Yǒu zhì zhě, shì jìng chéng",
                vietnamese = "Người có ý chí kiên cường thì việc ắt thành công.",
                meaning = "Chỉ cần bạn có quyết tâm chinh phục tiếng Trung, không có rào cản nào là không thể vượt qua.",
                author = "Hậu Hán Thư • 后汉书"
            ),
            DailyWisdom(
                chinese = "博学笃志，切问而近思",
                pinyin = "Bó xué dǔ zhì, qiè wèn ér jìn sī",
                vietnamese = "Học rộng giữ vững chí hướng, hỏi kỹ và suy nghĩ sâu xa.",
                meaning = "Học tập cần song hành giữa việc tiếp thu kiến thức rộng lớn và tự mình nghiền ngẫm thấu suốt.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            ),
            DailyWisdom(
                chinese = "同舟共济",
                pinyin = "Tóng zhōu gòng jì",
                vietnamese = "Chung thuyền qua sông, hoạn nạn cùng chia sẻ.",
                meaning = "Tinh thần đoàn kết, tương trợ lẫn nhau cùng tiến bước trên hành trình chinh phục ngôn ngữ.",
                author = "Tôn Tử • 孙子"
            ),
            DailyWisdom(
                chinese = "言必信，行必果",
                pinyin = "Yán bì xìn, xíng bì guǒ",
                vietnamese = "Lời nói ắt giữ chữ tín, hành động ắt làm đến nơi đến chốn.",
                meaning = "Đã đặt ra mục tiêu học tập mỗi ngày thì quyết tâm thực hiện trọn vẹn không trì hoãn.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            ),
            DailyWisdom(
                chinese = "亡羊补牢，未为晚也",
                pinyin = "Wáng yáng bǔ láo, wèi wéi wǎn yě",
                vietnamese = "Mất cừu rồi sửa chuồng, vẫn chưa phải là muộn.",
                meaning = "Bất cứ lúc nào nhận ra thiếu sót và bắt đầu sửa sai, đó đều là thời điểm vàng để học tập.",
                author = "Chiến Quốc Sách • 战国策"
            ),
            DailyWisdom(
                chinese = "岁寒，然后知松柏之后凋也",
                pinyin = "Suì hán, ránhòu zhī sōngbǎi zhī hòu diāo yě",
                vietnamese = "Mùa đông lạnh giá mới thấu hiểu cây tùng cây bách xanh tươi bền bỉ.",
                meaning = "Nghịch cảnh và áp lực là phép thử tốt nhất để tôn vinh lòng kiên định của người học.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            ),
            DailyWisdom(
                chinese = "学而不思则罔，思而不学则殆",
                pinyin = "Xué ér bù sī zé wǎng, sī ér bù xué zé dài",
                vietnamese = "Học mà không suy nghĩ thì mờ mịt, suy nghĩ mà không học thì nguy hại.",
                meaning = "Việc học tập ngôn ngữ cần kết hợp nhuần nhuyễn giữa tiếp thu bài giảng và chủ động tư duy phản xạ.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            ),
            DailyWisdom(
                chinese = "知之者不如好之者，好之者不如乐之者",
                pinyin = "Zhī zhī zhě bùrú hào zhī zhě, hào zhī zhě bùrú lè zhī zhě",
                vietnamese = "Biết học không bằng thích học, thích học không bằng say mê tìm thấy niềm vui trong học tập.",
                meaning = "Tìm thấy niềm vui và cảm hứng khi học tiếng Trung là chìa khóa để tiến bộ vượt bậc mỗi ngày.",
                author = "Khổng Tử • 孔子 (Luận Ngữ)"
            )
        )

        dailyWisdomRepository.saveAll(wisdoms)
        println(">>> Đã khởi tạo thành công ${wisdoms.size} câu thành ngữ & danh ngôn tiếng Trung phong phú!")
    }

    private fun initVocabularies() {
        vocabularyRepository.deleteAll()

        val list = listOf(
            // ── 1. Chào hỏi & Giao tiếp (3 từ) ──
            Vocabulary(
                hanzi = "你好",
                pinyin = "nǐ hǎo",
                hanViet = "Nhĩ Hảo",
                meaning = "Xin chào, chào bạn",
                partOfSpeech = "Thán từ / Cụm từ",
                topic = "Chào hỏi & Giao tiếp",
                radical = "Bộ: 亻 (Nhân đứng)",
                strokes = "7 nét",
                exampleHanzi = "你好，很高兴认识你！",
                examplePinyin = "Nǐ hǎo, hěn gāoxìng rènshí nǐ!",
                exampleMeaning = "Xin chào, rất vui được làm quen với bạn!",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "谢谢",
                pinyin = "xièxie",
                hanViet = "Tạ Tạ",
                meaning = "Cảm ơn",
                partOfSpeech = "Động từ",
                topic = "Chào hỏi & Giao tiếp",
                radical = "Bộ: 讠 (Ngôn)",
                strokes = "12 nét",
                exampleHanzi = "谢谢你的热情帮助！",
                examplePinyin = "Xièxie nǐ de rèqíng bāngzhù!",
                exampleMeaning = "Cảm ơn sự giúp đỡ nhiệt tình của bạn!",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "再见",
                pinyin = "zàijiàn",
                hanViet = "Tái Kiến",
                meaning = "Tạm biệt, hẹn gặp lại",
                partOfSpeech = "Cụm từ",
                topic = "Chào hỏi & Giao tiếp",
                radical = "Bộ: 冂 (Quynh)",
                strokes = "10 nét",
                exampleHanzi = "明天见，祝你一路平安，再见！",
                examplePinyin = "Míngtiān jiàn, zhù nǐ yílù píng'ān, zàijiàn!",
                exampleMeaning = "Ngày mai gặp lại, chúc bạn thượng lộ bình an, tạm biệt!",
                hskLevel = "HSK 1",
                isMastered = false
            ),

            // ── 2. Ăn uống & Ẩm thực (3 từ) ──
            Vocabulary(
                hanzi = "水",
                pinyin = "shuǐ",
                hanViet = "Thủy",
                meaning = "Nước, nước uống",
                partOfSpeech = "Danh từ",
                topic = "Ăn uống & Ẩm thực",
                radical = "Bộ: 水 (Thủy)",
                strokes = "4 nét",
                exampleHanzi = "我想喝一杯温水。",
                examplePinyin = "Wǒ xiǎng hē yì bēi wēn shuǐ.",
                exampleMeaning = "Tôi muốn uống một cốc nước ấm.",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "吃",
                pinyin = "chī",
                hanViet = "Cật",
                meaning = "Ăn, dùng bữa",
                partOfSpeech = "Động từ",
                topic = "Ăn uống & Ẩm thực",
                radical = "Bộ: 口 (Khẩu)",
                strokes = "6 nét",
                exampleHanzi = "你吃米饭还是面条？",
                examplePinyin = "Nǐ chī mǐfàn háishì miàntiáo?",
                exampleMeaning = "Bạn ăn cơm hay ăn mì?",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "茶",
                pinyin = "chá",
                hanViet = "Trà",
                meaning = "Trà, chè",
                partOfSpeech = "Danh từ",
                topic = "Ăn uống & Ẩm thực",
                radical = "Bộ: 艹 (Thảo)",
                strokes = "9 nét",
                exampleHanzi = "中国人很喜欢喝中国茶。",
                examplePinyin = "Zhōngguó rén hěn xǐhuan hē Zhōngguó chá.",
                exampleMeaning = "Người Trung Quốc rất thích uống trà Trung Quốc.",
                hskLevel = "HSK 1",
                isMastered = false
            ),

            // ── 3. Đi lại & Du lịch (3 từ) ──
            Vocabulary(
                hanzi = "去",
                pinyin = "qù",
                hanViet = "Khứ",
                meaning = "Đi, đến",
                partOfSpeech = "Động từ",
                topic = "Đi lại & Du lịch",
                radical = "Bộ: 厶 (Khứ)",
                strokes = "5 nét",
                exampleHanzi = "你明天去哪儿旅游？",
                examplePinyin = "Nǐ míngtiān qù nǎr lǚyóu?",
                exampleMeaning = "Ngày mai bạn đi đâu du lịch?",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "飞机",
                pinyin = "fēijī",
                hanViet = "Phi Cơ",
                meaning = "Máy bay",
                partOfSpeech = "Danh từ",
                topic = "Đi lại & Du lịch",
                radical = "Bộ: 飞 (Phi)",
                strokes = "11 nét",
                exampleHanzi = "我们坐飞机去北京。",
                examplePinyin = "Wǒmen zuò fēijī qù Běijīng.",
                exampleMeaning = "Chúng tôi đi máy bay đến Bắc Kinh.",
                hskLevel = "HSK 1",
                isMastered = false
            ),
            Vocabulary(
                hanzi = "出租车",
                pinyin = "chūzūchē",
                hanViet = "Xuất Tô Xa",
                meaning = "Xe taxi",
                partOfSpeech = "Danh từ",
                topic = "Đi lại & Du lịch",
                radical = "Bộ: 车 (Xa)",
                strokes = "19 nét",
                exampleHanzi = "在机场我们可以打出租车。",
                examplePinyin = "Zài jīchǎng wǒmen kěyǐ dǎ chūzūchē.",
                exampleMeaning = "Ở sân bay chúng ta có thể bắt xe taxi.",
                hskLevel = "HSK 1",
                isMastered = false
            ),

            // ── 4. Mua sắm & Trả giá (3 từ) ──
            Vocabulary(
                hanzi = "多少",
                pinyin = "duōshao",
                hanViet = "Đa Thiểu",
                meaning = "Bao nhiêu",
                partOfSpeech = "Đại từ",
                topic = "Mua sắm & Trả giá",
                radical = "Bộ: 夕 (Tịch)",
                strokes = "10 nét",
                exampleHanzi = "这个东西多少钱？",
                examplePinyin = "Zhège dōngxi duōshao qián?",
                exampleMeaning = "Cái này bao nhiêu tiền?",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "钱",
                pinyin = "qián",
                hanViet = "Tiền",
                meaning = "Tiền bạc, giá cả",
                partOfSpeech = "Danh từ",
                topic = "Mua sắm & Trả giá",
                radical = "Bộ: 钅 (Kim)",
                strokes = "10 nét",
                exampleHanzi = "我带的钱不够买这个。",
                examplePinyin = "Wǒ dài de qián bú gòu mǎi zhège.",
                exampleMeaning = "Tiền tôi mang không đủ mua món này.",
                hskLevel = "HSK 1",
                isMastered = false
            ),
            Vocabulary(
                hanzi = "块",
                pinyin = "kuài",
                hanViet = "Khối",
                meaning = "Đồng tệ (đơn vị tiền tệ khẩu ngữ)",
                partOfSpeech = "Lượng từ",
                topic = "Mua sắm & Trả giá",
                radical = "Bộ: 土 (Thổ)",
                strokes = "7 nét",
                exampleHanzi = "一杯奶茶十五块钱。",
                examplePinyin = "Yì bēi nǎichá shíwǔ kuài qián.",
                exampleMeaning = "Một cốc trà sữa mười lăm tệ.",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "便宜",
                pinyin = "piányi",
                hanViet = "Tiện Nghi",
                meaning = "Rẻ, giá cả phải chăng",
                partOfSpeech = "Tính từ",
                topic = "Mua sắm & Trả giá",
                radical = "Bộ: 亻 (Nhân đứng)",
                strokes = "9 nét",
                exampleHanzi = "这件衣服挺便宜的。",
                examplePinyin = "Zhè jiàn yīfu tǐng piányi de.",
                exampleMeaning = "Chiếc áo này khá là rẻ.",
                hskLevel = "HSK 2",
                targetScore = "HSK 2 (Mục tiêu 180–200/200 điểm)",
                isMastered = false
            ),

            // ── 5. Gia đình & Đời sống (3 từ) ──
            Vocabulary(
                hanzi = "家",
                pinyin = "jiā",
                hanViet = "Gia",
                meaning = "Nhà, gia đình",
                partOfSpeech = "Danh từ",
                topic = "Gia đình & Đời sống",
                radical = "Bộ: 宀 (Miên)",
                strokes = "10 nét",
                exampleHanzi = "我家在北京。",
                examplePinyin = "Wǒ jiā zài Běijīng.",
                exampleMeaning = "Nhà tôi ở Bắc Kinh.",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "爸爸",
                pinyin = "bàba",
                hanViet = "Ba Ba",
                meaning = "Bố, cha",
                partOfSpeech = "Danh từ",
                topic = "Gia đình & Đời sống",
                radical = "Bộ: 父 (Phụ)",
                strokes = "8 nét",
                exampleHanzi = "我爸爸是一名优秀的医生。",
                examplePinyin = "Wǒ bàba shì yì míng yōuxiù de yīshēng.",
                exampleMeaning = "Bố tôi là một bác sĩ xuất sắc.",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "妈妈",
                pinyin = "māma",
                hanViet = "Ma Ma",
                meaning = "Mẹ, má",
                partOfSpeech = "Danh từ",
                topic = "Gia đình & Đời sống",
                radical = "Bộ: 女 (Nữ)",
                strokes = "6 nét",
                exampleHanzi = "妈妈做的中国菜非常好吃。",
                examplePinyin = "Māma zuò de Zhōngguó cài fēicháng hǎochī.",
                exampleMeaning = "Món ăn Trung Quốc mẹ nấu rất ngon.",
                hskLevel = "HSK 1",
                isMastered = true
            ),

            // ── 6. Học tập & Giáo dục (3 từ) ──
            Vocabulary(
                hanzi = "书",
                pinyin = "shū",
                hanViet = "Thư",
                meaning = "Sách, cuốn sách",
                partOfSpeech = "Danh từ",
                topic = "Học tập & Giáo dục",
                radical = "Bộ: 乙 (Ất)",
                strokes = "4 nét",
                exampleHanzi = "桌子上有一本中文书。",
                examplePinyin = "Zhuōzi shàng yǒu yì běn zhōngwén shū.",
                exampleMeaning = "Trên bàn có một cuốn sách tiếng Trung.",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "学习",
                pinyin = "xuéxí",
                hanViet = "Học Tập",
                meaning = "Học tập, rèn luyện",
                partOfSpeech = "Động từ",
                topic = "Học tập & Giáo dục",
                radical = "Bộ: 子 (Tử)",
                strokes = "8 nét",
                exampleHanzi = "我们每天努力学习汉语。",
                examplePinyin = "Wǒmen měitiān nǔlì xuéxí hànyǔ.",
                exampleMeaning = "Chúng tôi mỗi ngày nỗ lực học tiếng Hán.",
                hskLevel = "HSK 1",
                isMastered = true
            ),
            Vocabulary(
                hanzi = "老师",
                pinyin = "lǎoshī",
                hanViet = "Lão Sư",
                meaning = "Thầy cô giáo",
                partOfSpeech = "Danh từ",
                topic = "Học tập & Giáo dục",
                radical = "Bộ: 耂 (Lão)",
                strokes = "6 nét",
                exampleHanzi = "王老师教我们发音和语法。",
                examplePinyin = "Wáng lǎoshī jiāo wǒmen fāyīn hé yǔfǎ.",
                exampleMeaning = "Thầy Vương dạy chúng tôi phát âm và ngữ pháp.",
                hskLevel = "HSK 1",
                isMastered = false
            ),

            // ── 7. Sức khỏe & Y tế (3 từ) ──
            Vocabulary(
                hanzi = "医院",
                pinyin = "yīyuàn",
                hanViet = "Y Viện",
                meaning = "Bệnh viện",
                partOfSpeech = "Danh từ",
                topic = "Sức khỏe & Y tế",
                radical = "Bộ: 匚 (Phương)",
                strokes = "9 nét",
                exampleHanzi = "他在医院工作。",
                examplePinyin = "Tā zài yīyuàn gōngzuò.",
                exampleMeaning = "Anh ấy làm việc ở bệnh viện.",
                hskLevel = "HSK 1",
                isMastered = false
            ),
            Vocabulary(
                hanzi = "医生",
                pinyin = "yīshēng",
                hanViet = "Y Sinh",
                meaning = "Bác sĩ",
                partOfSpeech = "Danh từ",
                topic = "Sức khỏe & Y tế",
                radical = "Bộ: 匚 (Phương)",
                strokes = "7 nét",
                exampleHanzi = "李医生非常认真负责。",
                examplePinyin = "Lǐ yīshēng fēicháng rènzhēn fùzé.",
                exampleMeaning = "Bác sĩ Lý rất cẩn thận và trách nhiệm.",
                hskLevel = "HSK 1",
                isMastered = false
            ),
            Vocabulary(
                hanzi = "药",
                pinyin = "yào",
                hanViet = "Dược",
                meaning = "Thuốc chữa bệnh",
                partOfSpeech = "Danh từ",
                topic = "Sức khỏe & Y tế",
                radical = "Bộ: 艹 (Thảo)",
                strokes = "9 nét",
                exampleHanzi = "记得按时吃药多喝水。",
                examplePinyin = "Jìde ànshí chī yào duō hē shuǐ.",
                exampleMeaning = "Nhớ uống thuốc đúng giờ và uống nhiều nước.",
                hskLevel = "HSK 1",
                isMastered = false
            )
        )

        vocabularyRepository.saveAll(list)
        println(">>> Đã khởi tạo thành công ${list.size} từ vựng mẫu bám sát chủ đề!")
    }
}
