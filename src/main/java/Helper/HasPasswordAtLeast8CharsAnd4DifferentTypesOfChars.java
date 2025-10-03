package Helper;

public class HasPasswordAtLeast8CharsAnd4DifferentTypesOfChars implements IIsPasswordSecure{
    @Override
    public boolean isValid(String password) {
        boolean containsUppercaseLetter = false;
        boolean containsLowerCaseLetter = false;
        boolean containsASpecialChar = false;
        boolean containsANumber = false;

        if(password.length() < 8)
        {
            return false;
        }

        for(char ch: password.toCharArray())
        {
            if(Character.isUpperCase(ch))
            {
                containsUppercaseLetter = true;
                continue;
            }
            if(Character.isLowerCase(ch))
            {
                containsLowerCaseLetter = true;
                continue;
            }
            if(Character.isDigit(ch))
            {
                containsANumber = true;
                continue;
            }

            if(this.isASpecialChar(ch))
            {
                containsASpecialChar = true;
                continue;
            }
        }


        return (containsLowerCaseLetter && containsUppercaseLetter && containsASpecialChar && containsANumber);
    }

    @Override
    public String getPWGuideline() {
        return "The Password has to be at least 8 Chars long and contains 4 different types of Chars";
    }

    private boolean isASpecialChar(char character)
    {
        char[] arr = {'%', '§', '&', '=', '.', ',', '-', '_', '*', '+', '~'};

        for(char ch: arr)
        {
            if(Character.compare(character, ch) == 0)
            {
                return true;
            }
        }

        return false;
    }


}

