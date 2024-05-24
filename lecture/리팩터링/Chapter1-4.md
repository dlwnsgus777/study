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

### 임시 변수를 질의 함수로 바꾸기
- 변수를 사용하면 반복해서 동일한 식을 계산하는 것을 피하고 이름을 사용해 의미를 표현할 수 있다.
- 긴 함수를 리팩터링할 때 
