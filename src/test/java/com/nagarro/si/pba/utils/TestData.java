package com.nagarro.si.pba.utils;

import com.nagarro.si.pba.dto.CategoryDTO;
import com.nagarro.si.pba.dto.CategorySettingsDTO;
import com.nagarro.si.pba.dto.ExpenseDTO;
import com.nagarro.si.pba.dto.IncomeDTO;
import com.nagarro.si.pba.dto.GroupDTO;
import com.nagarro.si.pba.dto.LoginDTO;
import com.nagarro.si.pba.dto.RegisterDTO;
import com.nagarro.si.pba.dto.RoleDTO;
import com.nagarro.si.pba.dto.ReportRequestDTO;
import com.nagarro.si.pba.dto.TransactionDTO;
import com.nagarro.si.pba.dto.TransactionFilterDTO;
import com.nagarro.si.pba.dto.UserDTO;
import com.nagarro.si.pba.model.Category;
import com.nagarro.si.pba.model.CategoryType;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.Expense;
import com.nagarro.si.pba.model.ExpenseCategory;
import com.nagarro.si.pba.model.Group;
import com.nagarro.si.pba.model.Income;
import com.nagarro.si.pba.model.RepetitionFlow;
import com.nagarro.si.pba.model.Role;
import com.nagarro.si.pba.model.RoleType;
import com.nagarro.si.pba.model.ReportType;
import com.nagarro.si.pba.model.Token;
import com.nagarro.si.pba.model.Transaction;
import com.nagarro.si.pba.model.TransactionType;
import com.nagarro.si.pba.model.User;
import com.nagarro.si.pba.security.PasswordEncoder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestData {
    private final static String testSecretKey = "2zCGWqdyNBzitg9NijPcA61819rfeSXg8ETEg3z8DNKmRgFn0EJa5YkBVf7QIcIA";
    private static final PasswordEncoder passwordEncoder = new PasswordEncoder();

    public static UserDTO returnUserDTOForDonJoe() {
        return new UserDTO(1, "donjoe@gmail.com", "donjoe", Currency.RON, "don", "joe",
                "colombia", 38);
    }

    public static UserDTO returnUserDTOForBobDumbledore() {
        return new UserDTO(2, "bobdumbledore@gmail.com", "bobdumbledore", Currency.RON, "bob", "dumbledore",
                "canada", 87);
    }

    public static UserDTO returnUserDTOForMichaelStevens() {
        return new UserDTO(3, "michaelstevens@gmail.com", "michaelstevens", Currency.RON, "michael", "stevens",
                "USA", 37);
    }

    public static User returnUserForDonJoe() {
        return new User(1, "donjoe@gmail.com", "donjoe", "donjoe123", "don", "joe",
                "colombia", 38, true, 0, Currency.RON);
    }

    public static User returnUserForDonJoeNotVerifiedAndHashed() {
        return new User(1, "donjoe@gmail.com", "donjoe", passwordEncoder.hashPassword("donjoe123"), "don", "joe",
                "colombia", 38, false, 0, Currency.RON);
    }

    public static User returnUserForBobDumbledore() {
        return new User(2, "bobdumbledore@gmail.com", "bobdumbledore", "bobdumbledore123", "bob", "dumbledore",
                "canada", 87, true, 0, Currency.RON);
    }

    public static User returnUserForMichaelStevens() {
        return new User(3, "michaelstevens@gmail.com", "michaelstevens", "michaelstevens123", "michael", "stevens",
                "USA", 37, true, 0, Currency.RON);
    }

    public static RegisterDTO returnRegisterDTOForDonJoe() {
        return new RegisterDTO("donjoe@gmail.com", "donjoe", "donjoe123", "don", "joe",
                "colombia", 38, Currency.RON);
    }

    public static RegisterDTO returnRegisterDTOForTomSawyer() {
        return new RegisterDTO("tomsawyer@gmail.com", "tomsawyer", "tomsawyer123", "tom", "sawyer",
                "Greece", 11, Currency.RON);
    }

    public static String getRegistrationEmailTemplate() {
        return "Hello %s %s,\n\nPlease verify your account by clicking on the following link:\n\n%s\n\nThe link is valid for 24 hours.";
    }

    public static String getResetPasswordEmailTemplate() {
        return "Hello %s %s,\n\nYour account is verified. You can reset the password using the token below.\n\n";
    }

    public static Token returnNewToken() {
        Token newToken = new Token();
        newToken.setId(1);
        newToken.setToken("newToken");
        return newToken;
    }

    public static UserDTO returnUserDTOFromRegisterDTO(RegisterDTO registerDTO) {
        return new UserDTO(
                null,
                registerDTO.email(),
                registerDTO.username(),
                registerDTO.defaultCurrency(),
                registerDTO.firstName(),
                registerDTO.lastName(),
                registerDTO.country(),
                registerDTO.age());
    }

    public static List<UserDTO> returnListOfUserDtos() {
        List<UserDTO> userDTOList = new ArrayList<>();

        userDTOList.add(returnUserDTOForDonJoe());
        userDTOList.add(returnUserDTOForBobDumbledore());
        userDTOList.add(returnUserDTOForMichaelStevens());

        return userDTOList;
    }

    public static List<User> returnListOfUsers() {
        List<User> users = new ArrayList<>();

        users.add(returnUserForDonJoe());
        users.add(returnUserForBobDumbledore());
        users.add(returnUserForMichaelStevens());

        return users;
    }

    public static String returnTokenFromLoginForDonJoe() {
        return "token";
    }

    public static LoginDTO returnLoginDTOForDonJoe() {
        return new LoginDTO("donjoe", "donjoe123");
    }

    public static LoginDTO returnLoginDTOForDonJoeWithInvalidPassword() {
        return new LoginDTO("donjoe", "invalidpw");
    }

    public static String ReturnExpiredToken() {
        Date expiration = new Date(System.currentTimeMillis() - 10000);
        return Jwts.builder()
                .setSubject("expired")
                .setExpiration(expiration)
                .signWith(getSignInKey())
                .compact();
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(TestData.testSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Token returnValidToken() {
        String validTokenStr = "valid-token";
        Token validToken = new Token();
        validToken.setId(1);
        validToken.setToken(validTokenStr);
        return validToken;
    }

    public static Category returnDefaultCategory() {
        Category category = new Category();
        category.setName("salary");
        category.setType(CategoryType.INCOME);
        category.setIsDefault(true);
        return category;
    }

    public static Category returnNonDefaultCategory() {
        Category category = new Category();
        category.setName("cadou");
        category.setType(CategoryType.EXPENSE);
        return category;
    }

    public static Category returnNewCategory() {
        Category category = new Category();
        category.setName("shopping");
        category.setType(CategoryType.EXPENSE);
        category.setIsDefault(false);
        category.setIsSelected(true);
        return category;
    }

    public static List<Category> returnListOfIncomeCategories() {
        return Arrays.asList(
                new Category(1, "salar", CategoryType.INCOME, true, true),
                new Category(2, "transfer", CategoryType.INCOME, false, true));
    }

    public static List<Category> returnListOfExpenseCategories() {
        return Arrays.asList(
                new Category(1, "party", CategoryType.EXPENSE, true, true),
                new Category(2, "pizza", CategoryType.EXPENSE, false, true));
    }

    public static List<CategoryDTO> returnListOfIncomeCategoryDTOs() {
        return Arrays.asList(
                new CategoryDTO(1, "salar", CategoryType.INCOME, true, true),
                new CategoryDTO(2, "transfer", CategoryType.INCOME, false, true));
    }

    public static List<CategoryDTO> returnListOfExpenseCategoryDTOs() {
        return Arrays.asList(
                new CategoryDTO(3, "party", CategoryType.EXPENSE, false, true),
                new CategoryDTO(4, "pizza", CategoryType.EXPENSE, true, true));
    }

    public static CategorySettingsDTO returnCategorySettingsDTO() {
        return new CategorySettingsDTO(returnListOfIncomeCategoryDTOs(), returnListOfExpenseCategoryDTOs());
    }

    public static CategorySettingsDTO returnSampleCategorySettingsDTO() {
        List<CategoryDTO> incomes = Arrays.asList(
                new CategoryDTO(1, "salary", CategoryType.INCOME, true, false),
                new CategoryDTO(2, "gifts", CategoryType.INCOME, true, false),
                new CategoryDTO(3, "scholarship", CategoryType.INCOME, true, false));

        List<CategoryDTO> expenses = Arrays.asList(
                new CategoryDTO(4, "rent", CategoryType.EXPENSE, true, false),
                new CategoryDTO(5, "groceries", CategoryType.EXPENSE, true, false),
                new CategoryDTO(6, "utilities", CategoryType.EXPENSE, true, false),
                new CategoryDTO(7, "entertainment", CategoryType.EXPENSE, true, false),
                new CategoryDTO(8, "healthcare", CategoryType.EXPENSE, true, false),
                new CategoryDTO(9, "education", CategoryType.EXPENSE, true, false));

        return new CategorySettingsDTO(incomes, expenses);
    }

    public static CategoryDTO getNewCategoryDTO() {
        return new CategoryDTO(1, "updatedSalary", CategoryType.INCOME, false, true);

    }

    public static IncomeDTO returnRecurringIncomeDTO() {
        return new IncomeDTO(1, "Salary", "MySalary", 300, "description", Currency.RON, LocalDateTime.of(2001, 9, 9, 15, 15), RepetitionFlow.MONTHLY, 1);
    }

    public static IncomeDTO returnRecurringIncomeJustForUserDTO() {
        return new IncomeDTO(1, "Salary", "MySalary", 300, "description", Currency.RON, LocalDateTime.of(2001, 9, 9, 15, 15), RepetitionFlow.MONTHLY, null);
    }

    public static IncomeDTO returnOneTimeIncomeDTO() {
        return new IncomeDTO(1, "Gift", "Chocolate", 300, "description", Currency.RON, LocalDateTime.of(2001, 9, 9, 15, 15), RepetitionFlow.NONE, 1);
    }

    public static Income returnOneTimeIncomeEntity() {
        return new Income(1, "Gift", "Chocolate", 200, "description", Currency.RON, LocalDateTime.of(2022, 3, 2, 2, 2), RepetitionFlow.NONE, 1, null);
    }


    public static Income returnRecurringIncomeEntity() {
        return new Income(1, "Salary", "MySalary", 300, "description", Currency.RON, LocalDateTime.of(2001, 9, 9, 15, 15), RepetitionFlow.MONTHLY, 1, null);
    }

    public static Income returnIncome() {
        return new Income(
                1,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.ANNUALLY,
                1,
                null);
    }

    public static Income returnIncomeNONE() {
        return new Income(
                1,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.NONE,
                1,
                null);
    }

    public static Income returnIncomeForGroup() {
        return new Income(
                1,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.ANNUALLY,
                1,
                1);
    }
    public static Expense returnExpenseForGroup() {
        return new Expense(
                1,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.NONE,
                1,
                1);
    }

    public static Income returnOnetimeIncome() {
        return new Income(
                1,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.NONE,
                1,
                null);
    }

    public static Expense returnExpense() {
        return new Expense(
                1,
                "Salary",
                "MySalary",
                200.00,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.MONTHLY,
                1,
                null);
    }


    public static Expense returnExpenseNONE() {
        return new Expense(
                1,
                "Salary",
                "MySalary",
                200.00,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.NONE,
                1,
                null);
    }


    public static TransactionDTO returnTransactionIncomeDTO() {
        return new TransactionDTO(
                1,
                TransactionType.INCOME,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.ANNUALLY,
                1);
    }

    public static TransactionDTO returnTransactionIncomeDTONONE() {
        return new TransactionDTO(
                1,
                TransactionType.INCOME,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.NONE,
                1);
    }

    public static TransactionDTO returnTransactionOnetimeIncomeDTO() {
        return new TransactionDTO(
                1,
                TransactionType.INCOME,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 3, 2, 2, 2),
                RepetitionFlow.NONE,
                1);
    }

    public static TransactionDTO returnTransactionExpenseDTO() {
        return new TransactionDTO(
                1,
                TransactionType.INCOME,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.MONTHLY,
                1);
    }

    public static TransactionDTO returnTransactionExpenseDTONONE() {
        return new TransactionDTO(
                1,
                TransactionType.EXPENSE,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.NONE,
                1);
    }


    public static List<Transaction> returnTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(returnIncome());
        transactions.add(returnExpense());

        return transactions;
    }

    public static List<TransactionDTO> returnTransactionDTOList() {
        List<TransactionDTO> transactions = new ArrayList<>();
        transactions.add(returnTransactionIncomeDTO());
        transactions.add(returnTransactionExpenseDTO());

        return transactions;
    }

    public static List<TransactionDTO> returnTransactionDTOListWithOnetimeIncome() {
        List<TransactionDTO> transactions = new ArrayList<>();
        transactions.add(returnTransactionOnetimeIncomeDTO());
        transactions.add(returnTransactionExpenseDTO());

        return transactions;
    }

    public static Expense returnRecurringExpenseEntity() {
        return new Expense(1, ExpenseCategory.SUBSCRIPTIONS.getValue(), "MyMusicSubscription", 25.0, "Spotify Monthly Subscription", Currency.RON, LocalDateTime.now(), RepetitionFlow.MONTHLY, 1, null);
    }

    public static ExpenseDTO returnRecurringExpenseDTO() {
        return new ExpenseDTO(8, ExpenseCategory.SUBSCRIPTIONS.getValue(), "MyMusicSubscription", 25.0, "Spotify Monthly Subscription", Currency.RON, LocalDateTime.now(), RepetitionFlow.MONTHLY, null);
    }

    public static Expense returnOneTimeExpenseEntity() {
        return new Expense(1, ExpenseCategory.SUBSCRIPTIONS.getValue(), "MyMusicSubscription", 25.0, "Spotify Subscription", Currency.RON, LocalDateTime.of(2022, 2, 2, 2, 2), RepetitionFlow.NONE, 1, null);
    }

    public static ExpenseDTO returnOneTimeExpenseDTO() {
        return new ExpenseDTO(1, ExpenseCategory.SUBSCRIPTIONS.getValue(), "MyMusicSubscription", 25.0, "Spotify Subscription", Currency.RON, LocalDateTime.now(), RepetitionFlow.NONE, null);
    }

    public static Group returnGroup() {
        return new Group(1, "test", Currency.RON);
    }

    public static GroupDTO returnGroupDTO() {
        return new GroupDTO(1, "test", Currency.RON);
    }

    public static Role returnRole() {
        return new Role(1, RoleType.ADMIN);
    }

    public static RoleDTO returnRoleDTO() {
        return new RoleDTO(1, RoleType.ADMIN);
    }

    public static User userWithHashedPasswordAndVerified() {
        return new User(2, "donjoe@gmail.com", "donjoe", passwordEncoder.hashPassword("donjoe123"), "don", "joe",
                "colombia", 38, true, 0, Currency.RON);
    }

    public static TransactionFilterDTO returnTransactionFilterDTO() {
        return new TransactionFilterDTO(
                LocalDateTime.of(2022, 2, 2, 2, 2),
                LocalDateTime.of(2022, 1, 8, 15, 15),
                LocalDateTime.of(2022, 1, 8, 15, 15)
        );
    }

    public static TransactionFilterDTO returnTransactionFilterDTOExactDate() {
        return new TransactionFilterDTO(
                LocalDateTime.of(2022, 3, 2, 2, 2),
                null,
                null
        );
    }

    public static TransactionFilterDTO returnTransactionFilterDTORangeDates() {
        return new TransactionFilterDTO(
                null,
                LocalDateTime.of(2022, 3, 2, 2, 1),
                LocalDateTime.of(2022, 4, 2, 2, 2)
        );
    }

    public static TransactionFilterDTO returnTransactionFilterDTOEmpty() {
        return new TransactionFilterDTO(
                null,
                null,
                null
        );
    }


    public static ReportRequestDTO returnIncomesReportRequestDTO() {
        return new ReportRequestDTO(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                ReportType.INCOMES_REPORT
        );
    }

    public static Object returnExpensesReportRequestDTO() {
        return new ReportRequestDTO(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                ReportType.EXPENSES_REPORT
        );
    }

    public static Object returnAllTransactionsReportRequestDTO() {
        return new ReportRequestDTO(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                ReportType.ALL_TRANSACTIONS_REPORT
        );
    }


    public static TransactionDTO returnNewTransactionExpenseDTOWithoutGroup() {
        return new TransactionDTO(
                1,
                TransactionType.EXPENSE,
                "Bill",
                "bill1",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.NONE,
                null);
    }

    public static Expense returnExpenseWithoutGroup() {
        return new Expense(
                1,
                "Bill",
                "bill1",
                200.00,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.NONE,
                1,
                null);

    }

    public static List<String> returnTransactionsHeader() {
        return Arrays.asList("Transaction Type", "Category", "Name", "Amount", "Description", "Currency", "Added Date","Balance After Transaction");
    }

    public static Group returnGroupId1() {
        return new Group(1, "test", Currency.RON);
    }

    public static TransactionDTO returnTransactionIncomeDTOWithoutGroup() {
        return new TransactionDTO(
                1,
                TransactionType.INCOME,
                "Salary",
                "MySalary",
                200,
                "description",
                Currency.RON,
                LocalDateTime.of(2022, 2, 2, 2, 2),
                RepetitionFlow.NONE,
                null);
    }
}