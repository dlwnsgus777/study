## 리팩터링 - 인프런 백기선님 강의 정리 (chapter1 ~ 4)
---


### 리팩터링
- 소프트웨어 행동은 유지하면서 코드를 유지보수하기 쉽게 개선하는 작업
    - 처음부터 완벽하게 시스템을 설계하는 것은 불가능
    - 코드를 변경하는 일이 자주 발생한다
    - 리팩터링을 통해 구조를 꾸준히 개선해 나가야 한다.
- 구조 변경으로 인한 버그를 줄이면서 코드를 깔끔하게 유지할 수 있는 방법

---

## Chapter1. 이해하기 힘든 이름

- 좋은 이름은 어떤 역할을 하는지 어떻게 쓰이는지 직관적으로 나타내야한다.
- 사용할 수 있는 리팩터링 기술
    - 함수 선언 변경하기
    - 변수 이름 바꾸기
    - 필드 이름 바꾸기

### 함수 선언 변경하기 (rename)
- 좋은 이름을 가진 함수는 어떻게 구현되었는지 이름만 보고도 이해할 수 있다.
    - 함수에 달린 주석을 기반으로 이름을 유추해보는 것도 좋은 방법
- 함수의 매개 변수는
    - 함수 내부의 문맥을 결정
    - 의존성을 결정
- 인텔리제이 단축키 : `Shift + F6`

```java
    /*
     * 스터디 리뷰 이슈에 작성되어 있는 리뷰어 목록과 리뷰를 읽어온다    
     */
    private void studyReviews(GHIssue issue) throws IOException {
        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            reviews.add(comment.getBody());
        }
    }
```

```java
    private void loadReviews(GHIssue issue) throws IOException {
        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            reviews.add(comment.getBody());
        }
    }
```

### 변수 이름 바꾸기 (rename)
- 많이 사용되는 변수일수록 이름이 중요하다.
- 동적타입 언어의 경우에는 이름에 타입을 넣기도한다.
- 인텔리제이 단축키 : `Shift + F6`

```java
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
```

```java
        studyDashboard.getUsernames().forEach(name -> System.out.println(name));
        studyDashboard.getReviews().forEach(review -> System.out.println(review));
```

### 필드 이름 바꾸기 (rename)
- 자바에서는 getter와 setter 메서드 이름도 필드의 이름과 비스시하게 간주할 수 있다.
- 인텔리제이 단축키 : `Shift + F6`

---
## Chapter2. 중복 코드
- 중복 코드의 단점
  - 비슷한지, 완전히 동일한 코드인지 주의 깊게 봐야한다.
  - 코드를 변경할 때 동일한 모든 곳의 코드를 변경해야 한다.
- 사용할 수 있는 리팩터링 기술
  - 함수 추출하기
  - 코드 정리하기
  - 메서드 올리

### 함수 추출하기 (Extract Function)
- __의도__ 와 __구현__ 을 분리하자.
- 무슨 일을 하는 코드인지 알아내려고 노력해야 하는 코드라면
  - 해당 코드를 함수로 분리
  - 함수 이름으로 무슨일을 하는지 표현
- 한줄 짜리 메서드를 따로 빼내는 경우도 괜찮다.
- 주석은 추출할 함수를 찾는데 좋은 단서가 된다.
- 인텔리제이 단축키 : `Option + Command + M`

```java
    private void printParticipants(int eventId) throws IOException {
        // Get github issue to check homework
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(eventId);

        // Get participants
        Set<String> participants = new HashSet<>();
        issue.getComments().forEach(c -> participants.add(c.getUserName()));

        // Print participants
        participants.forEach(System.out::println);
    }

```

```java
    private void printParticipants(int eventId) throws IOException {
        GHIssue issue = getGhIssue(eventId);
        Set<String> participants = getUsernames(issue);

        // Print participants
        participants.forEach(System.out::println);
    }

    private static Set<String> getUsernames(final GHIssue issue) throws IOException {
        Set<String> participants = new HashSet<>();
        issue.getComments().forEach(c -> participants.add(c.getUserName()));
        return participants;
    }

    private static GHIssue getGhIssue(final int eventId) throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(eventId);
        return issue;
    }
```

### 코드 분리하기 (Slide Statements)

- 관련있는 코드끼리 묶여있어야 코드를 이해하기 더 쉽다.
- 함수에서 사용할 변수를 상단에 미리 정의하기 보다는 **해당 변수를 사용하는 코드 바로 위**에 선언하자.
  - 변수를 사용되기 바로 직전에 선언해야한다
- 관련있는 코드끼리 묶은 다음 `함수 추출하기`를 사용해 더 깔끔하게 분리할 수 있다.
- 인텔리제이 단축키 : `Command + Shift + 방향키`

```java
private void printReviewers() throws IOException {
  // Get github issue to check homework
  Set<String> reviewers = new HashSet<>();
  GitHub gitHub = GitHub.connect();
  GHRepository repository = gitHub.getRepository("whiteship/live-study");
  GHIssue issue = repository.getIssue(30);

  // Get reviewers
  issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

  // Print reviewers
  reviewers.forEach(System.out::println);
}
```

```java
private void printReviewers() throws IOException {
  // Get github issue to check homework
  GitHub gitHub = GitHub.connect();
  GHRepository repository = gitHub.getRepository("whiteship/live-study");
  GHIssue issue = repository.getIssue(30);

  // Get reviewers
  Set<String> reviewers = new HashSet<>();
  issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

  // Print reviewers
  reviewers.forEach(System.out::println);
}
```

### 메서드 올리기 (Pull Up Method)
- 중복 코드는 당장은 잘 동작하더라도 미래에 버그를 만들어 낼 빌미를 제공한다.
- 여러 하위 클래스에 동일한 코드가 있다면 쉽게 적용할 수 있음
- 비슷하지만 일부 값만 다른 경우
  - `함수 매개변수화하기` 리팩터링을 적용한 이 후 메서드 올리기를 사용할 수 있다.
- 하위 클래스에 있는 코드가 상위 클래스가 아닌 하위 클래스 기능에 의존하고 있다면
  - `필드 올리기`를 적용한 이 후 메서드 올리기를 사용할 수 있다.
- 두 메서드가 비슷한 절차를 따르고 있다면 `템플릿 메서드 패턴` 적용을 고려해볼 수 있다.
- 인텔리제이 단축키: 없음
  - refactor > Pull Members Up 이라는 메뉴에서 진행

```java
public class Dashboard {

    public static void main(String[] args) throws IOException {
        ReviewerDashboard reviewerDashboard = new ReviewerDashboard();
        reviewerDashboard.printReviewers();

        ParticipantDashboard participantDashboard = new ParticipantDashboard();
        participantDashboard.printParticipants(15);
    }
}

public class ReviewerDashboard extends Dashboard {

  public void printReviewers() throws IOException {
    // Get github issue to check homework
    GitHub gitHub = GitHub.connect();
    GHRepository repository = gitHub.getRepository("whiteship/live-study");
    GHIssue issue = repository.getIssue(30);

    // Get reviewers
    Set<String> reviewers = new HashSet<>();
    issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

    // Print reviewers
    reviewers.forEach(System.out::println);
  }

}

public class ParticipantDashboard extends Dashboard {

  public void printParticipants(int eventId) throws IOException {
    // Get github issue to check homework
    Set<String> reviewers = new HashSet<>();
    GitHub gitHub = GitHub.connect();
    GHRepository repository = gitHub.getRepository("whiteship/live-study");
    GHIssue issue = repository.getIssue(30);

    // Get reviewers
    issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

    // Print reviewers
    reviewers.forEach(System.out::println);
  }
}

```

```java
public class Dashboard {

    public static void main(String[] args) throws IOException {
        ReviewerDashboard reviewerDashboard = new ReviewerDashboard();
        reviewerDashboard.printReviewers();

        ParticipantDashboard participantDashboard = new ParticipantDashboard();
        participantDashboard.printParticipants(15);
    }

    public void printUsernames(int eventId) throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(eventId);

        // Get reviewers
        Set<String> reviewers = new HashSet<>();
        issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

        // Print reviewers
        reviewers.forEach(System.out::println);
    }
}

public class ParticipantDashboard extends Dashboard {

  public void printParticipants(int eventId) throws IOException {
    printUsernames(eventId);
  }
}

public class ReviewerDashboard extends Dashboard {

  public void printReviewers() throws IOException {
    printUsernames(30);
  }
}
```

---

## Chapter3. 긴 함수

- 함수가 길 수록 더 이해하기 어렵다.
  - 짧은 함수는 더 많은 문맥 전환을 필요로 한다.
- "과거에는" 작은 함수를 많이 사용하기에는 성능에 무리가 있었다.
- 주석을 남기고 싶다면 주석 대신 함수를 만들고 함수의 이름으로 __의도__ 를 표현해보자.
- 사용할 수 있는 리팩터링 기술
  - 함수 추출하기
    - 함수 추출 중 매개변수가 많아진다면
      - 임시 변수를 질의 함수로 바꾸기
      - 매개변수 객체 만들기
      - 객체 통쨰로 넘기기 
  - 조건문 분해하기
  - 조건문을 다형성으로 바꾸기
  - 반복문 쪼개기

### 임시 변수를 질의 함수로 바꾸기 (Replace Temp with Query
- 변수를 사용하면 반복해서 동일한 식을 계산하는 것을 피하고 이름을 사용해 의미를 표현할 수 있다.
- 긴 함수를 리팩터링할 때 매개변수를 줄일 수 있다.

```java
        try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {
            participants.sort(Comparator.comparing(Participant::username));

            writer.print(header(totalNumberOfEvents, participants.size()));

            participants.forEach(p -> {
                long count = p.homework().values().stream()
                        .filter(v -> v == true)
                        .count();
                double rate = count * 100 / totalNumberOfEvents;

                String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), rate);
                writer.print(markdownForHomework);
            });
        }
```
```java
      try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {
            participants.sort(Comparator.comparing(Participant::username));

            writer.print(header(totalNumberOfEvents, participants.size()));

            participants.forEach(p -> {
                String markdownForHomework = getMarkdownForHomework(p, totalNumberOfEvents);
                writer.print(markdownForHomework);
            });
        }
    }

    private static double getRate(Participant p, int totalNumberOfEvents) {
        long count = p.homework().values().stream()
                .filter(v -> v == true)
                .count();
        double rate = count * 100 / totalNumberOfEvents;
        return rate;
    }

    private String getMarkdownForHomework(Participant p, int totalNumberOfEvents) {
        String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), getRate(p, totalNumberOfEvents));
        return markdownForHomework;
    }

```

### 매개변수 객체 만들기 (Introduce Parameter Object)

- 같은 매개변수들이 여러 메서드에 걸쳐 나타난다면 그 매개변수들을 묶은 자료 구조를 만들 수 있다.
  - 데이터간의 관계를 보다 명시적으로 나타낼 수 있다.
  - 함수에 전달할 매개변수 개수를 줄일 수 있다.
  - 도메인을 이해하는데 중요한 역할을 하는 클래스로 발전할 수 있다.

```java
// 생략
try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {
            participants.sort(Comparator.comparing(Participant::username));

            writer.print(header(totalNumberOfEvents, participants.size()));

            participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(totalNumberOfEvents, p);
                writer.print(markdownForHomework);
            });
        }

private double getRate(int totalNumberOfEvents, Participant p) {
        long count = p.homework().values().stream()
        .filter(v -> v == true)
        .count();
        double rate = count * 100 / totalNumberOfEvents;
        return rate;
        }

private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) {
        return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), getRate(totalNumberOfEvents, p));
        }
        
```

```java
public class ParticipantPrinter {
  private final Participant participant;
  private final int totalNumberOfEvents;

  public Map<Integer, Boolean> getHomework() {
    return participant.homework();
  }

  public String getUsername() {
    return participant.username();
  }

  public Integer getTotalNumberOfEvents() {
    return totalNumberOfEvents;
  }

  public double getRate() {
    long count = participant.homework().values().stream()
            .filter(v -> v == true)
            .count();
    double rate = count * 100 / totalNumberOfEvents;
    return rate;
  }
}

private String getMarkdownForParticipant(ParticipantPrinter p) {
  return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, p.getTotalNumberOfEvents()), p.getRate());
}
```

### 객체 통째로 넘기기 (Preserve Whole Object)
- 어떤 객체에서 구할 수 있는 여러 값들을 함수에 전달하는 경우 해당 매개변수를 객체 하나로 교체할 수 있다.
- 매개변수 목록을 줄일 수 있다.
- 의존성을 고려해야 한다.
  - 어쩌면 해당 메서드의 위치가 적절하지 않을 수 있다.

```java
String markdownForHomework = getMarkdownForParticipant(p.username(), p.homework());

private String getMarkdownForParticipant(String username, Map<Integer, Boolean> homework) {
        return String.format("| %s %s | %.2f%% |\n", username,
        checkMark(homework, this.totalNumberOfEvents),
        getRate(homework));
        }
```

```java
String markdownForHomework = getMarkdownForParticipant(p.username(), p.homework());

    private String getMarkdownForParticipant(Participant participant) {
        return String.format("| %s %s | %.2f%% |\n", participant.getUserName(),
                checkMark(participant.getHomework(), this.totalNumberOfEvents),
                getRate(participant.getHomework()));
    }

```

### 함수를 명령으로 바꾸기 (Replace Function with Command)
- 함수를 독립적인 객체인 `Command`로 만들어 사용할 수 있다.
  - 부가적인 기능으로 undo 기능을 만들 수도 있다.
  - 더 복잡한 기능을 구현하는데 필요한 여러 메서드를 추가할 수 있다.
  - 상속이나 템플릿을 활용할 수 있다.
  - 복잡한 메서드를 여러 메서드나 필드를 활용해 쪼갤 수 있다.
- `Command`말고 다른 방법이 없는 경우에만 사용한다

```java
   private void print() throws IOException, InterruptedException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        List<Participant> participants = new CopyOnWriteArrayList<>();

        ExecutorService service = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(totalNumberOfEvents);

        for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
        int eventId = index;
        service.execute(new Runnable() {
@Override
public void run() {
        try {
        GHIssue issue = repository.getIssue(eventId);
        List<GHIssueComment> comments = issue.getComments();

        for (GHIssueComment comment : comments) {
        Participant participant = findParticipant(comment.getUserName(), participants);
        participant.setHomeworkDone(eventId);
        }

        latch.countDown();
        } catch (IOException e) {
        throw new IllegalArgumentException(e);
        }
        }
        });
        }

        latch.await();
        service.shutdown();

        try (FileWriter fileWriter = new FileWriter("participants.md");
        PrintWriter writer = new PrintWriter(fileWriter)) {
        participants.sort(Comparator.comparing(Participant::username));

        writer.print(header(participants.size()));

        participants.forEach(p -> {
        String markdownForHomework = getMarkdownForParticipant(p);
        writer.print(markdownForHomework);
        });
        }
        }

private String getMarkdownForParticipant(Participant p) {
        return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, this.totalNumberOfEvents),
        p.getRate(this.totalNumberOfEvents));
        }
```
```java
private void print() throws IOException, InterruptedException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        List<Participant> participants = new CopyOnWriteArrayList<>();

        ExecutorService service = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(totalNumberOfEvents);

        for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
            int eventId = index;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        GHIssue issue = repository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();

                        for (GHIssueComment comment : comments) {
                            Participant participant = findParticipant(comment.getUserName(), participants);
                            participant.setHomeworkDone(eventId);
                        }

                        latch.countDown();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }

        latch.await();
        service.shutdown();

        new PrintStudy().execute(participants);
    }


public class StudyDashboard {

  private final int totalNumberOfEvents;

  public StudyDashboard(int totalNumberOfEvents) {
    this.totalNumberOfEvents = totalNumberOfEvents;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    StudyDashboard studyDashboard = new StudyDashboard(15);
    studyDashboard.print();
  }

  private Participant findParticipant(String username, List<Participant> participants) {
    Participant participant = null;
    if (participants.stream().noneMatch(p -> p.username().equals(username))) {
      participant = new Participant(username);
      participants.add(participant);
    } else {
      participant = participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
    }
    return participant;
  }

  private void print() throws IOException, InterruptedException {
    GitHub gitHub = GitHub.connect();
    GHRepository repository = gitHub.getRepository("whiteship/live-study");
    List<Participant> participants = new CopyOnWriteArrayList<>();

    ExecutorService service = Executors.newFixedThreadPool(8);
    CountDownLatch latch = new CountDownLatch(totalNumberOfEvents);

    for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
      int eventId = index;
      service.execute(new Runnable() {
        @Override
        public void run() {
          try {
            GHIssue issue = repository.getIssue(eventId);
            List<GHIssueComment> comments = issue.getComments();

            for (GHIssueComment comment : comments) {
              Participant participant = findParticipant(comment.getUserName(), participants);
              participant.setHomeworkDone(eventId);
            }

            latch.countDown();
          } catch (IOException e) {
            throw new IllegalArgumentException(e);
          }
        }
      });
    }

    latch.await();
    service.shutdown();

    new PrintStudy().execute(participants);
  }
}
```
### 조건문 분해하기 (Decompose Conditional)

- 여러 조건에 따라 달라지는 코드를 작성하다보면 종종 긴 함수가 만들어진다
- __조건__과 __액션__ 모두 __의도__를 표현해야 한다.
- `함수 추출하기`와 동일한 리팩터링이지만 의도만 다를 뿐이다.

```java
private Participant findParticipant(String username, List<Participant> participants) {
        Participant participant;
        if (participants.stream().noneMatch(p -> p.username().equals(username))) {
            participant = new Participant(username);
            participants.add(participant);
        } else {
            participant = participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
        }

        return participant;
    }
```

```java
private Participant findParticipant(String username, List<Participant> participants) {
        return isNewParticipant(username, participants) ? createNewParticipant(username, participants) : findExistingParticipant(username, participants);
        }

private static Participant findExistingParticipant(String username, List<Participant> participants) {
        return participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
        }

private static Participant createNewParticipant(String username, List<Participant> participants) {
        Participant participant;
        participant = new Participant(username);
        participants.add(participant);
        return participant;
        }

private static boolean isNewParticipant(String username, List<Participant> participants) {
        return participants.stream().noneMatch(p -> p.username().equals(username));
        }

```

### 반복문 쪼개기 (Split Loop)
- 하나의 반복문에서 여러 다른 작업을 하는 코드를 쉽게 찾아볼 수 있다.
  - 해당 반복문을 수정할 때 여러 작업을 모두 고려하며 코딩을 해야함
- 반복문을 여러개로 쪼개면 보다 쉽게 이해하고 수정할 수 있다.
- 성능 문제를 야기할 수 있지만 __리팩터링과 성능 최적화는 별개의 작업이다__
  - 리팩터링을 마친 이후에 성능 최적화를 시도할 수 있다.

```java
private void print() throws IOException, InterruptedException {
        GHRepository ghRepository = getGhRepository();

        ExecutorService service = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(totalNumberOfEvents);

        for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
            int eventId = index;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        GHIssue issue = ghRepository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();
                        Date firstCreatedAt = null;
                        Participant first = null;

                        for (GHIssueComment comment : comments) {
                            Participant participant = findParticipant(comment.getUserName(), participants);
                            participant.setHomeworkDone(eventId);

                            if (firstCreatedAt == null || comment.getCreatedAt().before(firstCreatedAt)) {
                                firstCreatedAt = comment.getCreatedAt();
                                first = participant;
                            }
                        }

                        firstParticipantsForEachEvent[eventId - 1] = first;
                        latch.countDown();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }
```

```java
private void print() throws IOException, InterruptedException {
        GHRepository ghRepository = getGhRepository();

        ExecutorService service = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(totalNumberOfEvents);

        for (int index = 1 ; index <= totalNumberOfEvents ; index++) {
            int eventId = index;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        GHIssue issue = ghRepository.getIssue(eventId);
                        List<GHIssueComment> comments = issue.getComments();
                        checkHomework(comments);
                        firstParticipantsForEachEvent[eventId - 1] = findFirst(comments);
                        latch.countDown();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }

        latch.await();
        service.shutdown();

        new StudyPrinter(this.totalNumberOfEvents, this.participants).execute();
        printFirstParticipants();
    }

    private static Participant findFirst(List<GHIssueComment> comments) throws IOException {
        Date firstCreatedAt = null;
        Participant first = null;
        for (GHIssueComment comment : comments) {
            if (firstCreatedAt == null || comment.getCreatedAt().before(firstCreatedAt)) {
                firstCreatedAt = comment.getCreatedAt();
                first = participant;
            }
        }
        return first;
    }

    private void checkHomework(List<GHIssueComment> comments) {
        for (GHIssueComment comment : comments) {
            Participant participant = findParticipant(comment.getUserName(), participants);
            participant.setHomeworkDone(eventId);
        }
    }
```

### 조건문을 다형성으로 바꾸기 (Replace Conditional with Polymorphism)

- 여러 타입에 따라 각기 다른 로직을 처리해야 하는 경우 다형성을 적용해서 조건문을 보다 명확하게 분리할 수 있다.
  - 반복되는 `switch`문을 제거할 수 있다.
- 공통으로 사용되는 로직은 상위 클래스에 두고 달라지는 부분만 하위 클래스에 둠으로써 달라지는 부분만 강조할 수 있다.
- 모든 조건문을 다형성으로 바꿔야 하는 것은 아니다.


---

## Chapter4. 긴 매개변수 목록

- 매개변수가 많을수록 함수의 역할을 이해하기 어려워진다.
  - 함수가 한가지 일만 하는게 맞는가?
  - 불필요한 매개변수는 없는가?
  - 하나의 객체로 뭉칠 수 있는 매개변수 목록은 없는가?

- 매개변수르 질의 함수로 바꾸기
  - 어떤 매개변수를 다른 매개변수를 통해 알아낼 수 있을 때
- 객체 통째로 넘기기
  - 기존 자료구조에서 세부적인 데이터를 가져와서 여러 매개변수로 넘기는 대신 사용
- 매개변수 객체 만들기
  - 일부 매개변수들이 대부분 같이 넘겨질 떄
- 플래그 인수 제거하기
  - 매개변수가 플래그로 사용될 때
- 여러 함수를 클래스로 묶기
  - 여러 함수가 일부 매개 변수를 공통적으로 사용할 때

### 매개변수를 질의 함수로 바꾸기 (Replace Parameter with Query)

- 매개변수 목록은 함수의 다양성을 대변하며 짧을수록 __이해하기 좋다.__
- 어떤 한 매개변수가 다른 매개변수를 통해 알아낼 수 있다면 중복 매개변수라 생각할 수 있다.
- 매개변수에 값을 전달하는 것은 __함수를 호출하는 쪽__ 의 책임이다.
  - 함수를 호출하는 쪽의 책임을 줄이고 함수 내부에서 책임지도록 노력한다.
- `임시 번수를 질의 함수로 바꾸기` 와 `함수 선언 변경하기`를 통해 적용한다.

```java
public class Order {

    private int quantity;

    private double itemPrice;

    public Order(int quantity, double itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double finalPrice() {
        double basePrice = this.quantity * this.itemPrice;
        int discountLevel = this.quantity > 100 ? 2 : 1;
        return this.discountedPrice(basePrice, discountLevel);
    }

    private double discountedPrice(double basePrice, int discountLevel) {
        return discountLevel == 2 ? basePrice * 0.90 : basePrice * 0.95;
    }
}
```

```java
public class Order {

    private int quantity;

    private double itemPrice;

    public Order(int quantity, double itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double finalPrice() {
        double basePrice = this.quantity * this.itemPrice;
        return this.discountedPrice(basePrice);
    }

    private int discountLevel() {
        return this.quantity > 100 ? 2 : 1;
    }

    private double discountedPrice(double basePrice) {
        return discountLevel() == 2 ? basePrice * 0.90 : basePrice * 0.95;
    }
}

```

### 플래그 인수 제거하기 (Remove Flag Argument)
- 플래그는 보통 함수에 매개변수로 전달해서 함수 내부의 로직을 분기하는데 사용한다.
- 플래그를 사용한 함수는 차이를 파악하기 어렵다.
- 조건문 분해하기를 활용할 수 있다.

```java
public class Shipment {

    public LocalDate deliveryDate(Order order, boolean isRush) {
        if (isRush) {
            int deliveryTime = switch (order.getDeliveryState()) {
                case "WA", "CA", "OR" -> 1;
                case "TX", "NY", "FL" -> 2;
                default -> 3;
            };
            return order.getPlacedOn().plusDays(deliveryTime);
        } else {
            int deliveryTime = switch (order.getDeliveryState()) {
                case "WA", "CA" -> 2;
                case "OR", "TX", "NY" -> 3;
                default -> 4;
            };
            return order.getPlacedOn().plusDays(deliveryTime);
        }
    }
}
```

```java
public class Shipment {

    public LocalDate regularDeliveryDate(Order order) {
        int deliveryTime = switch (order.getDeliveryState()) {
            case "WA", "CA" -> 2;
            case "OR", "TX", "NY" -> 3;
            default -> 4;
        };
        return order.getPlacedOn().plusDays(deliveryTime);
    }

    public LocalDate rushDeliveryDate(Order order) {
        int deliveryTime = switch (order.getDeliveryState()) {
            case "WA", "CA", "OR" -> 1;
            case "TX", "NY", "FL" -> 2;
            default -> 3;
        };
        return order.getPlacedOn().plusDays(deliveryTime);
    }
}
```

### 여러 함수를 클래스로 묶기 (Combine Functions into Class)

- 비슷한 매개변수 목록을 여러 함수에서 사용하고 있다면 해당 메서드를 모아서 클래스를 만들 수 있다.
- 클래스 내부로 메서드를 옮기고 데이터를 필드로 만들면 메서드에 전달해야 하는 매개변수 목록도 줄일 수 있다.