package seedu.duke.operationlist;

import seedu.duke.exceptions.SkyControlException;
import seedu.duke.terminalinfo.PassengerInfo;
import seedu.duke.ui.Ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassengerList extends OperationList {
    protected static final String REGEX_LETTER = "[a-zA-Z ]*";
    protected static final String REGEX_NUMBER = "[0-9]*";
    protected static final String REGEX_TIME = "([01]\\d|2[0-3])[0-5]\\d";
    protected static final String NAME_DELIMITER = "n/";
    protected static final String DEPARTURE_TIME_DELIMITER = " dt/";
    protected static final String FLIGHT_NUMBER_DELIMITER = " fn/";
    protected static final String GATE_NUMBER_DELIMITER = " gn/";
    protected static final String BOARDING_GROUP_DELIMITER = " bg/";
    protected static final String SEAT_NUMBER_DELIMITER = " sn/";
    protected static final String BOARDING_TIME_DELIMITER = " bt/";
    protected static final String FORWARD_SLASH = "/";
    protected static final String EMPTY_STRING = "";
    public static final int NO_PASSENGER = 0;
    protected static final int NAME_LENGTH_LIMIT = 24;
    protected static final int FN_MIN_LENGTH = 4;
    public static final int FIRST_INDEX = 0;
    public static final int SECOND_INDEX = 1;
    public static final int MIN_FN_LENGTH = 2;
    public static final int MAX_FN_LENGTH = 4;
    public static final int SEAT_NUM_SECOND_INDEX = 1;
    public static final int SEAT_NUM_FIRST_INDEX = 0;
    public static final int SEAT_NUM_THIRD_INDEX = 2;
    protected static boolean isNamePresent = false;
    protected static boolean isFlightNumberPresent = false;
    protected static boolean isSeatNumberPresent = false;
    protected static boolean isValidPassenger = false;
    protected static boolean isDeleteSuccess = false;
    protected static boolean isExceedNameLength = false;
    protected static boolean isWrongNameFormat = false;
    protected static boolean isWrongBoardingTimeFormat = false;
    protected static boolean isWrongBoardingGroupFormat = false;
    protected static boolean isWrongSeatNumberFormat = false;
    protected String name;
    protected String departureDate;
    protected String departureTime;
    protected String flightNumber;
    protected String gateNumber;
    protected String seatNumber;
    protected String boardingTime;
    protected int numOfPassengers = 0;
    protected int boardingGroup;
    protected static int startIndex;

    public PassengerList() {
    }

    //@@author shengiv
    @Override
    public void addOperation(String passengerDetail) throws SkyControlException {
        getNumberOfPassengers();
        getPassengerDetails(passengerDetail);
        validateDetailFormat();
        checkPassengerDetails();
        checkPassengerDuplicate();
        PassengerInfo passenger = new PassengerInfo(name, departureTime, flightNumber,
                gateNumber, boardingGroup, seatNumber, boardingTime);
        passengers.add(passenger);
        Ui.showAddedPassenger(passenger);
    }

    @Override
    public void delayFlightDeparture(String flightNum, String newDepartureTime) {
        modifyPassengersDepartureTime(flightNum, newDepartureTime);
    }

    @Override
    public void modifyFlightNum(String flightNum, String newFlightNum) {
        modifyPassengersFlightNum(flightNum, newFlightNum);
    }

    @Override
    public void modifyGateNum(String flightNum, String newGateNum) {
        modifyPassengersGateNum(flightNum, newGateNum);
    }

    //@@author ivanthengwr
    private void checkPassengerDetails() throws SkyControlException {
        if (isExceedNameLength) {
            throw new SkyControlException(ui.getExceedNameLengthError(name));
        } else if (isWrongNameFormat) {
            throw new SkyControlException(ui.getNameError());
        } else if (isWrongBoardingTimeFormat) {
            throw new SkyControlException(ui.getErrorMessage());
        } else if (isWrongBoardingGroupFormat) {
            throw new SkyControlException(ui.getBoardingGroupError());
        } else if (isWrongSeatNumberFormat) {
            throw new SkyControlException(ui.getSeatNumberError());
        }
        resetCheckFormat();
    }

    //@@author shengiv
    private void checkPassengerDuplicate() throws SkyControlException {
        for (int i = 0; i < numOfPassengers; i++) {
            validatePassenger(i);
            if (isPassengerDuplicate()) {
                resetCheckDuplicate();
                throw new SkyControlException(ui.getDuplicatePassengerError());
            }
        }
    }

    private void modifyPassengersFlightNum(String flightNum, String newFlightNum) {
        getNumberOfPassengers();
        for (int i = 0; i < numOfPassengers; i++) {
            PassengerInfo passenger = passengers.get(i);
            if (passenger.getFlightNumber().equals(flightNum)) {
                passenger.setFlightNumber(newFlightNum);
            }
        }
    }

    private void modifyPassengersGateNum(String flightNum, String newGateNum) {
        getNumberOfPassengers();
        for (int i = 0; i < numOfPassengers; i++) {
            PassengerInfo passenger = passengers.get(i);
            if (passenger.getFlightNumber().equals(flightNum)) {
                passenger.setGateNumber(newGateNum);
            }
        }
    }

    private void modifyPassengersDepartureTime(String flightNum, String newDepartureTime) {
        getNumberOfPassengers();
        for (int i = 0; i < numOfPassengers; i++) {
            PassengerInfo passenger = passengers.get(i);
            if (passenger.getFlightNumber().equals(flightNum)) {
                modifyDepartureAndBoardingTime(newDepartureTime, passenger);
            }
        }
    }

    private static void modifyDepartureAndBoardingTime(String newDepartureTime, PassengerInfo passenger) {
        passenger.setDepartureTime(newDepartureTime);
        String reformatDepartureTime = passenger.reformatDepartureTime(newDepartureTime);
        String newBoardingTime = passenger.getBoardTime(reformatDepartureTime);
        passenger.setBoardingTime(newBoardingTime);
    }

    //@@author ivanthengwr
    @Override
    public void deleteOperation(String passengerDetail) throws SkyControlException {
        getNumberOfPassengers();
        getPassengerName(passengerDetail);
        getFlightNumber(passengerDetail);
        getSeatNumber(passengerDetail);
        for (int i = 0; i < numOfPassengers; i++) {
            validatePassenger(i);
            if (isValidPassenger) {
                passengers.remove(i);
                getNumberOfPassengers();
                Ui.showDeleteMessage(name, flightNumber, seatNumber, numOfPassengers);
                isDeleteSuccess = true;
                break;
            }
        }
        if (!isDeleteSuccess) {
            throw new SkyControlException(ui.getDeleteError());
        }
        isDeleteSuccess = false;
    }

    @Override
    public void listOperation() {
        getNumberOfPassengers();
        ui.showPassengerListHeader();
        if (numOfPassengers == NO_PASSENGER) {
            ui.showEmptyPassengerList();
        }
        for (int i = 0; i < numOfPassengers; i++) {
            iteratePassengerDetail(i);
            ui.showPassengerListBody(name, departureDate, departureTime,
                    flightNumber, gateNumber, boardingGroup, seatNumber, boardingTime);
        }
        System.out.print("\n");
    }

    private void validatePassenger(int index) {
        getNumberOfPassengers();
        assert index < numOfPassengers;
        isNamePresent = passengers.get(index).getName().equals(name);
        isFlightNumberPresent = passengers.get(index).getFlightNumber().equals(flightNumber);
        isSeatNumberPresent = passengers.get(index).getSeatNumber().equals(seatNumber);
        isValidPassenger = isNamePresent && isFlightNumberPresent && isSeatNumberPresent;
    }

    public void getNumberOfPassengers() {
        numOfPassengers = passengers.size();
    }

    public void getPassengerDetails(String passengerDetail) throws SkyControlException {
        getPassengerName(passengerDetail);
        getDepartureDate();
        getFlightNumber(passengerDetail);
        getBoardingGroup(passengerDetail);
        getSeatNumber(passengerDetail);
        getDepartureTime(passengerDetail);
        getGateNumber(passengerDetail);
        getBoardingTime(passengerDetail);
    }

    private void iteratePassengerDetail(int index) {
        getNumberOfPassengers();
        assert index < numOfPassengers;
        name = passengers.get(index).getName();
        departureDate = PassengerInfo.getDepartureDate();
        departureTime = passengers.get(index).getDepartureTime();
        flightNumber = passengers.get(index).getFlightNumber();
        gateNumber = passengers.get(index).getGateNumber();
        boardingGroup = passengers.get(index).getBoardingGroup();
        seatNumber = passengers.get(index).getSeatNumber();
        boardingTime = passengers.get(index).getBoardingTime();
    }

    //@@author shengiv
    private void getPassengerName(String passengerDetail) throws SkyControlException {
        if (isAdd) {
            name = getSubstringBetweenDelimiters(passengerDetail,
                    NAME_DELIMITER, FLIGHT_NUMBER_DELIMITER);
        } else if (isDelete) {
            name = getSubstringBetweenDelimiters(passengerDetail,
                    NAME_DELIMITER, FLIGHT_NUMBER_DELIMITER);
        }
    }

    private void getDepartureDate() {
        departureDate = PassengerInfo.getDepartureDate();
    }

    private void getDepartureTime(String passengerDetail) throws SkyControlException {
        departureTime = getSubstringBetweenDelimiters(passengerDetail,
                DEPARTURE_TIME_DELIMITER, GATE_NUMBER_DELIMITER);

    }

    private void getFlightNumber(String passengerDetail) throws SkyControlException {
        if (isAdd) {
            flightNumber = getSubstringBetweenDelimiters(passengerDetail,
                    FLIGHT_NUMBER_DELIMITER, BOARDING_GROUP_DELIMITER);
        } else if (isDelete) {
            flightNumber = getSubstringBetweenDelimiters(passengerDetail,
                    FLIGHT_NUMBER_DELIMITER, SEAT_NUMBER_DELIMITER);
        }
    }

    public String getFlightNumberForSync(String passengerDetail) throws SkyControlException {
        flightNumber = getSubstringBetweenDelimiters(passengerDetail,
                FLIGHT_NUMBER_DELIMITER, BOARDING_GROUP_DELIMITER);
        if (!isValidFlightNumber(flightNumber)) {
            if (flightNumber.contains(FORWARD_SLASH)) {
                throw new SkyControlException(ui.getErrorMessage());
            } else {
                throw new SkyControlException(ui.getFlightNumberError());
            }
        }
        return flightNumber;
    }

    private void getGateNumber(String passengerDetail) throws SkyControlException {
        gateNumber = getSubstringBetweenDelimiters(passengerDetail,
                GATE_NUMBER_DELIMITER, BOARDING_TIME_DELIMITER);
    }

    private void getBoardingGroup(String passengerDetail) throws SkyControlException {
        String boardingGroupDetail = getSubstringBetweenDelimiters(passengerDetail,
                BOARDING_GROUP_DELIMITER, SEAT_NUMBER_DELIMITER);
        try {
            boardingGroup = Integer.parseInt(boardingGroupDetail);
        } catch (Exception e) {
            throw new SkyControlException(ui.getErrorMessage());
        }
        assert boardingGroup >= 0 : "Invalid boarding group";
    }

    private void getSeatNumber(String passengerDetail) throws SkyControlException {
        if (isAdd) {
            seatNumber = getSubstringBetweenDelimiters(passengerDetail,
                    SEAT_NUMBER_DELIMITER, DEPARTURE_TIME_DELIMITER);
        } else if (isDelete) {
            int indexOfSeatNumber = passengerDetail.indexOf(SEAT_NUMBER_DELIMITER);
            int indexOfFlightNumber = passengerDetail.indexOf(FLIGHT_NUMBER_DELIMITER);
            int startIndex = indexOfSeatNumber + SEAT_NUMBER_DELIMITER.length();
            if (indexOfSeatNumber < 0 || indexOfSeatNumber < indexOfFlightNumber) {
                throw new SkyControlException(ui.getErrorMessage());
            }
            seatNumber = passengerDetail.substring(startIndex).toUpperCase();
            if (seatNumber.equals(EMPTY_STRING)) {
                throw new SkyControlException(ui.getMissingDetailsError());
            }
        }
    }

    private void getBoardingTime(String passengerDetail) {
        int indexOfBoardingTime = passengerDetail.indexOf(BOARDING_TIME_DELIMITER);
        startIndex = indexOfBoardingTime + BOARDING_TIME_DELIMITER.length();
        boardingTime = passengerDetail.substring(startIndex).toUpperCase();

    }

    private static String getSubstringBetweenDelimiters(String inputString, String startDelimiter, String endDelimiter)
            throws SkyControlException {
        int indexOfStartDelimiter = inputString.indexOf(startDelimiter);
        int startIndex = indexOfStartDelimiter + startDelimiter.length();
        int endIndex = inputString.lastIndexOf(endDelimiter);
        if (endIndex < startIndex || endIndex < 0 || indexOfStartDelimiter < 0) {
            throw new SkyControlException(ui.getErrorMessage());
        }
        String outputString = inputString.substring(startIndex, endIndex).trim();
        if (outputString.equals(EMPTY_STRING)) {
            throw new SkyControlException(ui.getMissingDetailsError());
        }
        return outputString.toUpperCase();
    }

    //@@author ivanthengwr
    private void validateDetailFormat() {
        isExceedNameLength = name.length() > NAME_LENGTH_LIMIT;
        isWrongNameFormat = !name.matches(REGEX_LETTER);
        isWrongBoardingTimeFormat = isValidTime(boardingTime);
        isWrongBoardingGroupFormat = isValidBoardingGroup();
        isWrongSeatNumberFormat = isValidSeatNumber();
    }

    private boolean isValidSeatNumber() {
        boolean isNotValidLength = seatNumber.length() != 3;
        boolean isNotValidTag = !Character.isLetter(seatNumber.charAt(SEAT_NUM_THIRD_INDEX));
        boolean isNotValidNumber = !Character.isDigit(seatNumber.charAt(SEAT_NUM_FIRST_INDEX))
                || !Character.isDigit(seatNumber.charAt(SEAT_NUM_SECOND_INDEX));
        if (isNotValidLength) {
            return true;
        } else if (isNotValidTag) {
            return true;
        } else {
            return isNotValidNumber;
        }
    }

    private boolean isValidBoardingGroup() {
        String boardingGroupStr = String.valueOf(boardingGroup);
        boolean isNotValidNumber = !boardingGroupStr.matches(REGEX_NUMBER);
        boolean isExceedNumber = boardingGroup > 10;
        if (isNotValidNumber) {
            return true;
        } else {
            return isExceedNumber;
        }
    }

    private boolean isValidTime(String time) {
        Pattern p = Pattern.compile(REGEX_TIME);
        Matcher m = p.matcher(time);
        boolean isNotValidTime = !m.matches();
        return isNotValidTime;
    }

    private boolean isValidFlightNumber(String flightNumber) {
        int lenOfFlightNum = flightNumber.length();
        if (lenOfFlightNum < FN_MIN_LENGTH) {
            return false;
        } else if (isValidFlightNumberTag(flightNumber)) {
            int numOfDigits = checkNumOfDigits();
            return isValidFLightNum(numOfDigits);
        }
        return false;
    }

    private boolean isPassengerDuplicate() {
        boolean isPassengerDuplicate;
        isPassengerDuplicate = isFlightNumberPresent
                && isSeatNumberPresent;
        return isPassengerDuplicate;
    }

    private int checkNumOfDigits() {
        int numOfDigits = 0;
        for (int i = 0; i < flightNumber.length(); i++) {
            if (Character.isDigit(flightNumber.charAt(i))) {
                numOfDigits++;
            }
        }
        return numOfDigits;
    }

    private static boolean isValidFlightNumberTag(String flightNumber) {
        boolean isValidFlightNumberTag;
        isValidFlightNumberTag = Character.isLetter(flightNumber.charAt(FIRST_INDEX))
                && Character.isLetter(flightNumber.charAt(SECOND_INDEX));
        return isValidFlightNumberTag;
    }

    private static boolean isValidFLightNum(int numOfDigits) {
        boolean isValidFlightNumber;
        isValidFlightNumber = numOfDigits >= MIN_FN_LENGTH
                && numOfDigits <= MAX_FN_LENGTH;
        return isValidFlightNumber;
    }

    private void resetCheckFormat() {
        isExceedNameLength = false;
        isWrongNameFormat = false;
        isWrongBoardingTimeFormat = false;
        isWrongBoardingGroupFormat = false;
        isWrongSeatNumberFormat = false;
    }

    private void resetCheckDuplicate() {
        isFlightNumberPresent = false;
        isSeatNumberPresent = false;
    }
}
