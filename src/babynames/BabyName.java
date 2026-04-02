package babynames;

public class BabyName implements Comparable<BabyName> {
    private String name;
    private String gender;
    private int count;
    private int rank;

    public BabyName(String name, String gender, int count, int rank) {
        this.name = name;
        this.gender = gender;
        this.count = count;
        this.rank = rank;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getCount() { return count; }
    public int getRank() { return rank; }

    @Override
    public int compareTo(BabyName other) {
        // Сортування по збільшенню номера в рейтингу (1, 2, 3...)
        return Integer.compare(this.rank, other.rank);
    }

    @Override
    public String toString() {
        return String.format("Рейтинг: %d | Ім'я: %s | Стать: %s | Кількість: %d", rank, name, gender, count);
    }
}