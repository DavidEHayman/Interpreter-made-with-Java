package Lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);  
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch(c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '>': addToken(match('=')? GREATER_EQUAL : GREATER); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL: EQUAL); break;
            case '"': string(); break;

            case '\t': break;
            case '\r': break;
            case ' ': break;
            case '\n': line ++; break;

            case '/': if(match('/')) {
                // this case is for comments
                while(peek() != '\n' && !isAtEnd()){
                    advance();
                }
            } else if(match('*')){
                comment();
            } else {
                addToken(SLASH);
            }

            case 'o': if(match('r')){
                addToken(OR);
            } else{
                identifier();
            }
            break;

            default:
                if(isDigit(c)){
                    number();
                } else if (isAlpha(c)){
                    identifier();
                }
                else{
                    Main.error(line, "Unexpected character");
                }
        }
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean match(char expected) {
        if(isAtEnd()){
            return false;
        }
        if(source.charAt(current) != expected){
            return false;
        }
        current++;
        return true;
    }

    private char peek() {
        if(isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n'){
                line++;
            }
            advance();
        }
        if(isAtEnd()){
            Main.error(line, "Unterminated String");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isDigit(char num){
        return num <= '9' && num >= '0';
    }

    private void comment() {
        while(!isAtEnd()){
            if(peek() == '\n') line++;
            
            if(peek() == '*' && Nextpeek() == '/'){
                advance();
                advance();

                return;
            }
            advance();
        }
        Main.error(line, "Unclosed comment");
    }

    private void number() {
        while(isDigit(peek())){
            advance();
        }

        if(peek() == '.' && isDigit(Nextpeek())) {
            while(isDigit(peek()))
            advance();
        }
        addToken(NUMBER , Double.parseDouble(source.substring(start, current)));
    }

    private char Nextpeek() {
        if(current + 1 >= source.length()){
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private void identifier() {
        while(isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);

        if(type == null) type = IDENTIFIER;
            addToken(type);
        
    }

    private boolean isAlphaNumeric(char c) {
        return isDigit(c) || isAlpha(c);
    }






}
