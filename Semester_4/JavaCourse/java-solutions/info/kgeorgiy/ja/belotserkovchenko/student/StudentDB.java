package info.kgeorgiy.ja.belotserkovchenko.student;

import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StudentDB implements GroupQuery {
    private static final Comparator<Student> STUDENT_COMPARATOR =
            Comparator.comparing(Student::getLastName, Comparator.reverseOrder())
                    .thenComparing(Student::getFirstName, Comparator.reverseOrder()).thenComparing(Student::getId);

    private static final Comparator<Map.Entry<GroupName, Integer>> BASE_ENTRY_COMPARATOR =
            Map.Entry.comparingByValue();

    private static final Comparator<Map.Entry<GroupName, Integer>> ASCENDING_ENTRY_COMPARATOR =
            BASE_ENTRY_COMPARATOR.thenComparing(s -> s.getKey().name());

    private static final Comparator<Map.Entry<GroupName, Integer>> DESCENDING_ENTRY_COMPARATOR =
            BASE_ENTRY_COMPARATOR.thenComparing(
                    Comparator.comparing((Map.Entry<GroupName, Integer> s) -> s.getKey().name()).reversed());

    private <T> List<T> getFieldImpl(List<Student> students, Function<Student, T> fieldMethod) {
        return students.stream().map(fieldMethod).toList();
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getFieldImpl(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getFieldImpl(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getFieldImpl(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getFieldImpl(students, s -> s.getFirstName() + " " + s.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Comparator.comparingInt(Student::getId)).map(Student::getFirstName).orElse("");
    }

    private List<Student> sortStudentsImpl(
            Collection<Student> students, Comparator<Student> studentComparator) {
        return students.stream().sorted(studentComparator).toList();
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudentsImpl(students, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return students.stream().sorted(STUDENT_COMPARATOR).toList();
    }

    private <T> List<Student> findStudentByFieldImpl(
            Collection<Student> students, T sample, Function<Student, T> fieldMethod) {
        return students.stream()
                .filter(s -> Objects.nonNull(s) && Objects.equals(fieldMethod.apply(s), sample))
                .sorted(STUDENT_COMPARATOR).toList();
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudentByFieldImpl(students, name, Student::getFirstName);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudentByFieldImpl(students, name, Student::getLastName);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return findStudentByFieldImpl(students, group, Student::getGroup);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return students.stream().filter(s -> Objects.nonNull(s) && Objects.equals(s.getGroup(), group))
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName,
                        BinaryOperator.minBy(String::compareTo)));
    }

    private List<Group> getGroupsImpl(Collection<Student> students,
                                      Comparator<Group> groupComparator,
                                      Comparator<Student> studentComparator) {
        return students.stream().sorted(studentComparator)
                .collect(Collectors.groupingBy(Student::getGroup))
                .entrySet().stream().map(s -> new Group(s.getKey(), s.getValue()))
                .sorted(groupComparator).toList();
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getGroupsImpl(students, Comparator.comparing(Group::getName), STUDENT_COMPARATOR);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getGroupsImpl(students, Comparator.comparing(Group::getName), Comparator.comparing(Student::getId));
    }

    // :NOTE: не очевидно из названия что делает метод
    private <T> GroupName getResult(Map<GroupName, T> map, Comparator<Map.Entry<GroupName, T>> entryComp) {
        return map.entrySet().stream().max(entryComp).map(Map.Entry::getKey).orElse(null);
    }

    private GroupName getLargestGroupImpl(
            Collection<Student> students,
            Collector<? super Student, ?, Map<GroupName, Integer>> collector,
            Comparator<Map.Entry<GroupName, Integer>> entryComp) {
        return getResult(students.stream().collect(collector), entryComp);
    }

    @Override
    public GroupName getLargestGroup(Collection<Student> students) {
        return getLargestGroupImpl(students,
                Collectors.groupingBy(Student::getGroup,
                        Collectors.collectingAndThen(Collectors.toList(), List::size)),
                ASCENDING_ENTRY_COMPARATOR);
    }

    @Override
    public GroupName getLargestGroupFirstName(Collection<Student> students) {
        return getLargestGroupImpl(students,
                Collectors.groupingBy(Student::getGroup,
                        Collectors.mapping(Student::getFirstName,
                                Collectors.collectingAndThen(Collectors.toSet(), Set::size))),
                DESCENDING_ENTRY_COMPARATOR);
    }
}
