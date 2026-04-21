package com.examportal.config;

import com.examportal.model.Exam;
import com.examportal.model.Question;
import com.examportal.model.Question.Option;
import com.examportal.model.Question.QuestionType;
import com.examportal.model.Question.Difficulty;
import com.examportal.model.User;
import com.examportal.repository.ExamRepository;
import com.examportal.repository.QuestionRepository;
import com.examportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository     userRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository     examRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminId = seedAdmin();
        seedStudent();
        List<String> javaQids   = seedJavaQuestions(adminId);
        List<String> mathQids   = seedMathQuestions(adminId);
        List<String> sqlQids    = seedSqlQuestions(adminId);
        List<String> pythonQids = seedPythonQuestions(adminId);
        List<String> webQids    = seedWebDevQuestions(adminId);
        List<String> dsaQids    = seedDsaQuestions(adminId);
        seedExams(adminId, javaQids, mathQids, sqlQids, pythonQids, webQids, dsaQids);
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    private String seedAdmin() {
        return userRepository.findByEmail("admin@exam.com").map(u -> {
            log.info("[Seeder] Admin already exists — skipping.");
            return u.getId();
        }).orElseGet(() -> {
            User admin = new User();
            admin.setName("Demo Admin");
            admin.setEmail("admin@exam.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setActive(true);
            admin.setEmailVerified(true);
            User saved = userRepository.save(admin);
            log.info("[Seeder] ✅ Demo admin  → admin@exam.com / admin123");
            return saved.getId();
        });
    }

    private void seedStudent() {
        if (userRepository.existsByEmail("student@exam.com")) return;
        User s = new User();
        s.setName("Demo Student");
        s.setEmail("student@exam.com");
        s.setPassword(passwordEncoder.encode("student123"));
        s.setRole(User.Role.STUDENT);
        s.setActive(true);
        s.setEmailVerified(true);
        userRepository.save(s);
        log.info("[Seeder] ✅ Demo student → student@exam.com / student123");
    }

    // ── Question Sets ─────────────────────────────────────────────────────────

    private List<String> seedJavaQuestions(String adminId) {
        if (questionRepository.countBySubjectAndIsInQuestionBank("Java", true) > 0)
            return questionRepository.findBySubjectAndIsInQuestionBank("Java", true).stream().map(Question::getId).toList();

        return saveQuestions(List.of(
            q("What is the default value of an int in Java?",
              opts("0","null","undefined","1"), "a", Difficulty.EASY, "Primitive int defaults to 0.", "Variables"),
            q("Which keyword is used to inherit a class in Java?",
              opts("implements","extends","inherits","super"), "b", Difficulty.EASY, "'extends' is for classes, 'implements' for interfaces.", "OOP"),
            q("What does JVM stand for?",
              opts("Java Virtual Memory","Java Visual Machine","Java Virtual Machine","Java Verified Module"), "c", Difficulty.EASY, "JVM = Java Virtual Machine.", "Core"),
            q("Which is NOT a Java access modifier?",
              opts("public","private","protected","friend"), "d", Difficulty.MEDIUM, "'friend' is C++. Java has public/private/protected/package-private.", "Access Control"),
            q("Output of: System.out.println(10 / 3)?",
              opts("3.33","3","4","Error"), "b", Difficulty.MEDIUM, "Integer division discards the remainder.", "Operators"),
            q("Which interface must be implemented to create a thread?",
              opts("Runnable","Callable","Thread","Executable"), "a", Difficulty.MEDIUM, "Implement Runnable, pass to Thread constructor.", "Concurrency"),
            q("Average time complexity of HashMap.get()?",
              opts("O(n)","O(log n)","O(1)","O(n²)"), "c", Difficulty.HARD, "HashMap uses hashing → O(1) average.", "Collections"),
            q("Which creates an immutable list in Java?",
              opts("new ArrayList<>()","Arrays.asList()","List.of()","Collections.unmodifiableList(new ArrayList<>())"), "c", Difficulty.HARD, "List.of() throws on any modification attempt.", "Collections")
        ), "Java", adminId);
    }

    private List<String> seedMathQuestions(String adminId) {
        if (questionRepository.countBySubjectAndIsInQuestionBank("Mathematics", true) > 0)
            return questionRepository.findBySubjectAndIsInQuestionBank("Mathematics", true).stream().map(Question::getId).toList();

        return saveQuestions(List.of(
            q("Value of π to 2 decimal places?",
              opts("3.14","3.41","3.16","2.14"), "a", Difficulty.EASY, "π ≈ 3.14159", "Constants"),
            q("Square root of 144?",
              opts("11","12","13","14"), "b", Difficulty.EASY, "12 × 12 = 144", "Arithmetic"),
            q("Solve: 2x + 5 = 13. x = ?",
              opts("3","4","5","6"), "b", Difficulty.EASY, "2x = 8 → x = 4", "Algebra"),
            q("Derivative of sin(x)?",
              opts("cos(x)","-cos(x)","tan(x)","-sin(x)"), "a", Difficulty.MEDIUM, "d/dx[sin(x)] = cos(x)", "Calculus"),
            q("Triangle angles: 60°, 70°, x°. Find x.",
              opts("40°","50°","60°","70°"), "b", Difficulty.MEDIUM, "180 - 60 - 70 = 50°", "Geometry"),
            q("15% of 200?",
              opts("25","30","35","40"), "b", Difficulty.EASY, "15/100 × 200 = 30", "Percentages"),
            q("Integral of 2x dx?",
              opts("x² + C","2x² + C","x + C","2 + C"), "a", Difficulty.HARD, "∫2x dx = x² + C", "Calculus"),
            q("Prime numbers between 1 and 20?",
              opts("6","7","8","9"), "c", Difficulty.MEDIUM, "2,3,5,7,11,13,17,19 → 8 primes", "Number Theory")
        ), "Mathematics", adminId);
    }

    private List<String> seedSqlQuestions(String adminId) {
        if (questionRepository.countBySubjectAndIsInQuestionBank("SQL", true) > 0)
            return questionRepository.findBySubjectAndIsInQuestionBank("SQL", true).stream().map(Question::getId).toList();

        return saveQuestions(List.of(
            q("Which SQL clause filters rows?",
              opts("ORDER BY","GROUP BY","WHERE","HAVING"), "c", Difficulty.EASY, "WHERE filters rows; HAVING filters groups.", "Clauses"),
            q("What does SELECT DISTINCT do?",
              opts("Selects all rows","Selects unique rows","Deletes duplicates","Orders results"), "b", Difficulty.EASY, "DISTINCT removes duplicate rows.", "Queries"),
            q("Which join returns all rows from both tables?",
              opts("INNER JOIN","LEFT JOIN","RIGHT JOIN","FULL OUTER JOIN"), "d", Difficulty.MEDIUM, "FULL OUTER JOIN returns all rows, NULLs where no match.", "Joins"),
            q("What is a PRIMARY KEY?",
              opts("Allows NULL","Uniquely identifies each row","A foreign key reference","An index key"), "b", Difficulty.EASY, "PRIMARY KEY is unique and NOT NULL.", "Constraints"),
            q("Which function counts non-NULL values?",
              opts("SUM()","AVG()","COUNT()","MAX()"), "c", Difficulty.EASY, "COUNT(column) counts non-NULL values.", "Aggregates"),
            q("ACID stands for?",
              opts("Atomicity Consistency Isolation Durability","Accuracy Consistency Integration Durability","Atomicity Concurrency Isolation Durability","Accuracy Concurrency Integration Data"), "a", Difficulty.MEDIUM, "ACID = Atomicity, Consistency, Isolation, Durability.", "Transactions"),
            q("Which removes a table entirely?",
              opts("DELETE","TRUNCATE","DROP","REMOVE"), "c", Difficulty.MEDIUM, "DROP removes structure + data. TRUNCATE keeps structure.", "DDL"),
            q("What is a subquery?",
              opts("Query inside another query","Backup query","Stored procedure","View"), "a", Difficulty.MEDIUM, "A subquery (nested query) is SELECT inside another SQL statement.", "Advanced")
        ), "SQL", adminId);
    }

    private List<String> seedPythonQuestions(String adminId) {
        if (questionRepository.countBySubjectAndIsInQuestionBank("Python", true) > 0)
            return questionRepository.findBySubjectAndIsInQuestionBank("Python", true).stream().map(Question::getId).toList();

        return saveQuestions(List.of(
            q("Which symbol is used for single-line comments in Python?",
              opts("//","/* */","#","--"), "c", Difficulty.EASY, "Python uses # for single-line comments.", "Syntax"),
            q("What is the output of: print(type([]))?",
              opts("<class 'tuple'>","<class 'list'>","<class 'dict'>","<class 'set'>"), "b", Difficulty.EASY, "[] is a list literal.", "Data Types"),
            q("Which keyword creates an anonymous function in Python?",
              opts("def","fun","lambda","anon"), "c", Difficulty.EASY, "lambda creates anonymous (unnamed) functions.", "Functions"),
            q("What does len([1,2,3]) return?",
              opts("2","3","4","Error"), "b", Difficulty.EASY, "len() returns the number of elements → 3.", "Built-ins"),
            q("Which of these is immutable in Python?",
              opts("list","dict","tuple","set"), "c", Difficulty.MEDIUM, "Tuples are immutable; lists, dicts, and sets are mutable.", "Data Types"),
            q("What is a Python decorator?",
              opts("A loop construct","A function that modifies another function","A data type","An error handler"), "b", Difficulty.MEDIUM, "Decorators wrap functions to add behaviour.", "Functions"),
            q("Which module is used for regular expressions in Python?",
              opts("regex","re","regexp","rx"), "b", Difficulty.MEDIUM, "import re provides regular expression support.", "Standard Library"),
            q("Time complexity of list.append() in Python?",
              opts("O(n)","O(log n)","O(1) amortised","O(n²)"), "c", Difficulty.HARD, "Dynamic array append is O(1) amortised.", "Performance")
        ), "Python", adminId);
    }

    private List<String> seedWebDevQuestions(String adminId) {
        if (questionRepository.countBySubjectAndIsInQuestionBank("Web Development", true) > 0)
            return questionRepository.findBySubjectAndIsInQuestionBank("Web Development", true).stream().map(Question::getId).toList();

        return saveQuestions(List.of(
            q("What does HTML stand for?",
              opts("Hyper Text Markup Language","High Text Machine Language","Hyper Transfer Markup Language","Hyperlink Text Markup Language"), "a", Difficulty.EASY, "HTML = HyperText Markup Language.", "HTML"),
            q("Which CSS property changes text colour?",
              opts("font-color","text-color","color","foreground"), "c", Difficulty.EASY, "The 'color' property sets text colour in CSS.", "CSS"),
            q("Which HTTP method is used to send form data?",
              opts("GET","POST","PUT","DELETE"), "b", Difficulty.EASY, "POST sends data in the request body.", "HTTP"),
            q("What does API stand for?",
              opts("Application Programming Interface","Applied Program Integration","Automated Processing Interface","Application Process Integration"), "a", Difficulty.EASY, "API = Application Programming Interface.", "Concepts"),
            q("What does the 'async' keyword do in JavaScript?",
              opts("Makes code synchronous","Declares a function that returns a Promise","Blocks the thread","Imports a module"), "b", Difficulty.MEDIUM, "async functions always return a Promise.", "JavaScript"),
            q("Which status code means 'Not Found'?",
              opts("200","301","403","404"), "d", Difficulty.EASY, "404 = resource not found.", "HTTP"),
            q("What is the CSS Box Model? (innermost to outermost)",
              opts("Content → Padding → Border → Margin","Margin → Border → Padding → Content","Content → Margin → Border → Padding","Padding → Content → Margin → Border"), "a", Difficulty.MEDIUM, "Content is innermost, then Padding, Border, Margin.", "CSS"),
            q("What is CORS?",
              opts("Cross-Origin Resource Sharing","Cross-Object Routing System","Content Object Request Sharing","Client-Origin Resource System"), "a", Difficulty.MEDIUM, "CORS controls which origins can access your API.", "Security")
        ), "Web Development", adminId);
    }

    private List<String> seedDsaQuestions(String adminId) {
        if (questionRepository.countBySubjectAndIsInQuestionBank("Data Structures", true) > 0)
            return questionRepository.findBySubjectAndIsInQuestionBank("Data Structures", true).stream().map(Question::getId).toList();

        return saveQuestions(List.of(
            q("Which data structure uses LIFO order?",
              opts("Queue","Stack","LinkedList","Tree"), "b", Difficulty.EASY, "Stack is Last-In-First-Out (LIFO).", "Stack"),
            q("Which data structure uses FIFO order?",
              opts("Stack","Queue","Heap","Graph"), "b", Difficulty.EASY, "Queue is First-In-First-Out (FIFO).", "Queue"),
            q("Time complexity of Binary Search?",
              opts("O(n)","O(n²)","O(log n)","O(1)"), "c", Difficulty.MEDIUM, "Binary search halves the search space each step → O(log n).", "Searching"),
            q("Which sorting algorithm has best average time complexity?",
              opts("Bubble Sort","Insertion Sort","Merge Sort","Selection Sort"), "c", Difficulty.MEDIUM, "Merge Sort is O(n log n) average and worst case.", "Sorting"),
            q("What is a Hash Collision?",
              opts("Two keys map to the same hash","A hash function error","Memory overflow","A key not found"), "a", Difficulty.MEDIUM, "Collision = two different keys produce the same hash value.", "Hashing"),
            q("Worst-case time complexity of QuickSort?",
              opts("O(n log n)","O(n)","O(n²)","O(log n)"), "c", Difficulty.HARD, "QuickSort degrades to O(n²) on an already-sorted array with naive pivot.", "Sorting"),
            q("Which tree property does a Binary Search Tree maintain?",
              opts("Left child > parent","Left child < parent < right child","All leaves are at same depth","Parent < both children"), "b", Difficulty.MEDIUM, "BST: left < parent, right > parent.", "Trees"),
            q("Space complexity of DFS on a graph with V vertices?",
              opts("O(V²)","O(V)","O(1)","O(E)"), "b", Difficulty.HARD, "DFS uses a stack of depth V in the worst case → O(V).", "Graphs")
        ), "Data Structures", adminId);
    }

    // ── Sample Exams ──────────────────────────────────────────────────────────

    private void seedExams(String adminId,
                           List<String> javaQids, List<String> mathQids,
                           List<String> sqlQids,  List<String> pythonQids,
                           List<String> webQids,  List<String> dsaQids) {

        createExamIfMissing(adminId, "Java Basics Assessment",
            "Test your core Java knowledge covering OOP, JVM internals, Collections and Concurrency.",
            "Java", 30, javaQids, Exam.ExamStatus.ACTIVE, true, true, 2);

        createExamIfMissing(adminId, "Mathematics Fundamentals Quiz",
            "Covers arithmetic, algebra, geometry, percentages and introductory calculus.",
            "Mathematics", 45, mathQids, Exam.ExamStatus.ACTIVE, true, false, 1);

        createExamIfMissing(adminId, "SQL Essentials Test",
            "Evaluate your SQL knowledge — queries, joins, constraints, aggregates and transactions.",
            "SQL", 25, sqlQids, Exam.ExamStatus.ACTIVE, false, true, 1);

        createExamIfMissing(adminId, "Python Programming Quiz",
            "Test your Python skills — syntax, data types, functions, decorators and performance.",
            "Python", 30, pythonQids, Exam.ExamStatus.ACTIVE, true, true, 2);

        createExamIfMissing(adminId, "Web Development Fundamentals",
            "HTML, CSS, JavaScript, HTTP methods, APIs and browser security concepts.",
            "Web Development", 25, webQids, Exam.ExamStatus.ACTIVE, true, false, 1);

        createExamIfMissing(adminId, "Data Structures & Algorithms",
            "Stacks, queues, trees, graphs, sorting algorithms and time complexity analysis.",
            "Data Structures", 40, dsaQids, Exam.ExamStatus.ACTIVE, true, true, 1);

        // Mixed aptitude — sample questions from each subject
        List<String> mixedIds = new ArrayList<>();
        safePick(javaQids, 2, mixedIds);
        safePick(mathQids, 2, mixedIds);
        safePick(sqlQids,  2, mixedIds);
        safePick(pythonQids, 2, mixedIds);
        safePick(webQids, 2, mixedIds);
        createExamIfMissing(adminId, "General Aptitude Test",
            "Mixed aptitude covering Java, Math, SQL, Python and Web Dev — ideal for full-stack screening.",
            "Mixed", 30, mixedIds, Exam.ExamStatus.ACTIVE, true, true, 3);

        // One DRAFT exam to show the Draft state in admin panel
        createExamIfMissing(adminId, "Advanced DSA Challenge (Draft)",
            "An advanced challenge on graphs, dynamic programming and amortised analysis — work in progress.",
            "Data Structures", 60, dsaQids, Exam.ExamStatus.DRAFT, false, false, 1);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void createExamIfMissing(String adminId, String title, String description,
                                     String subject, int durationMinutes,
                                     List<String> questionIds, Exam.ExamStatus status,
                                     boolean shuffleQ, boolean shuffleO, int maxAttempts) {
        if (examRepository.findByTitle(title).isPresent()) {
            log.info("[Seeder] Exam '{}' already exists — skipping.", title);
            return;
        }
        Exam e = new Exam();
        e.setTitle(title);
        e.setDescription(description);
        e.setSubject(subject);
        e.setDurationMinutes(durationMinutes);
        e.setTotalMarks(questionIds.size());
        e.setPassingMarks((int) Math.ceil(questionIds.size() * 0.6));
        e.setQuestionIds(new ArrayList<>(questionIds));
        e.setStatus(status);
        e.setShuffleQuestions(shuffleQ);
        e.setShuffleOptions(shuffleO);
        e.setMaxAttempts(maxAttempts);
        e.setCreatedBy(adminId);
        examRepository.save(e);
        log.info("[Seeder] ✅ Exam created: '{}' [{}]", title, status);
    }

    private void safePick(List<String> src, int n, List<String> dest) {
        if (src != null) dest.addAll(src.subList(0, Math.min(n, src.size())));
    }

    private Object[] q(String text, List<Option> options, String correct,
                       Difficulty diff, String explanation, String topic) {
        return new Object[]{text, options, correct, diff, explanation, topic};
    }

    private List<Option> opts(String a, String b, String c, String d) {
        return List.of(new Option("a", a), new Option("b", b),
                       new Option("c", c), new Option("d", d));
    }

    @SuppressWarnings("unchecked")
    private List<String> saveQuestions(List<Object[]> raw, String subject, String adminId) {
        List<String> ids = new ArrayList<>();
        for (Object[] r : raw) {
            Question q = new Question();
            q.setText((String) r[0]);
            q.setOptions((List<Option>) r[1]);
            q.setCorrectAnswer((String) r[2]);
            q.setDifficulty((Difficulty) r[3]);
            q.setExplanation((String) r[4]);
            q.setTopic((String) r[5]);
            q.setType(QuestionType.MCQ);
            q.setMarks(1);
            q.setSubject(subject);
            q.setInQuestionBank(true);
            q.setCreatedBy(adminId);
            ids.add(questionRepository.save(q).getId());
        }
        log.info("[Seeder] ✅ {} {} questions added to bank.", ids.size(), subject);
        return ids;
    }
}
