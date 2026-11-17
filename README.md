# DevOps Practical 2: Jenkins Freestyle Job with Maven

**LYCS DevOps Sem 7, Q2: Setting up Jenkins and a CI job (30 marks)**

Repository: https://github.com/show-star/aa2

## Aim

Install Jenkins, then create a Jenkins **Freestyle job** named `java-build-job`. The job pulls this Java Maven project from GitHub, checks GitHub for new commits every 2 minutes (**Poll SCM**), and builds the project with `mvn clean package`.

## What this practical is about

- **Jenkins** is an automation server used for **CI (Continuous Integration)**. Every time code changes, Jenkins builds and tests it automatically, so mistakes are caught early.
- A **Freestyle job** is the simplest kind of Jenkins job. You set it up by filling in forms: where the code is, when to build, and which build command to run.
- **Maven** is a Java build tool. It reads `pom.xml`, downloads the libraries the project needs (here, JUnit), compiles the code, runs the tests and packages everything into a `.jar` file.
- **Poll SCM** means Jenkins checks the Git repository on a schedule and starts a build when it finds a new commit.

## What's in this repo

```
aa2/
├── pom.xml                                  Maven project file (Java 17, JUnit 4)
├── src/main/java/com/example/App.java       The program: adds two numbers
└── src/test/java/com/example/AppTest.java   JUnit test that checks add(2, 3) = 5
```

| Maven command | What it does |
| --- | --- |
| `mvn clean` | Deletes the old `target` folder |
| `mvn compile` | Compiles `App.java` |
| `mvn test` | Runs `AppTest` |
| `mvn package` | Compiles, tests and creates `target/java-maven-demo-1.0.jar` |

---

## Part 0: Install and start Jenkins (Q2.1, 6 marks)

Skip this part if Jenkins already opens at `http://localhost:8080`.

### Step 1: Install Java 21

1. Press the **Windows key**, type **PowerShell**, then click **Run as administrator** and click **Yes**.
2. Run:
   ```powershell
   winget install EclipseAdoptium.Temurin.21.JDK
   ```
3. Close PowerShell, open a new one and check:
   ```powershell
   java -version
   ```
   It should print a version starting with **21**.

If `winget` isn't recognized, download **Temurin 21 (.msi)** from https://adoptium.net and install it. During the install, set **Set JAVA_HOME variable** to *Will be installed*.

### Step 2: Install Git

1. Download **Git for Windows** from https://git-scm.com and install it with the default options.
2. Check it in a new Command Prompt: `git --version`

### Step 3: Install Jenkins

1. Go to https://www.jenkins.io/download and, under **LTS**, download the **Windows** installer (`.msi`).
2. Run the installer and click **Next**.
3. **Logon type:** choose **Run service as LocalSystem**, then **Next**.
4. **Port:** keep **8080**. Click **Test Port**, then **Next**.
5. **Java home:** choose the JDK 21 folder, for example `C:\Program Files\Eclipse Adoptium\jdk-21...`, then **Next**.
6. Click **Install**, then **Finish**.

### Step 4: First-time setup

1. Open Chrome and go to **http://localhost:8080**. The **Unlock Jenkins** page appears.
2. Open this file in Notepad and copy the password inside it:
   ```
   C:\ProgramData\Jenkins\.jenkins\secrets\initialAdminPassword
   ```
3. Paste the password into the page and click **Continue**.
4. Click **Install suggested plugins** and wait until every plugin shows a green tick.
5. Fill in the **Create First Admin User** form (username, password, full name and email), then click **Save and Continue**. Write down the password.
6. Keep the Jenkins URL as `http://localhost:8080/`. Click **Save and Finish**, then **Start using Jenkins**.
7. Restart Jenkins so it can find Git. Open PowerShell or Command Prompt **as Administrator** and run:
   ```
   net stop jenkins
   net start jenkins
   ```

📸 **Screenshots:** `java -version`, the Unlock Jenkins page, plugins installing, Create First Admin User, and the Jenkins dashboard.

---

## Part A: Add Maven to Jenkins (one time)

1. In Jenkins, click **Manage Jenkins**, then **Tools**.
2. Scroll down to **Maven installations** and click **Add Maven**.
3. **Name:** `Maven3` (type exactly this).
4. Tick **Install automatically** and keep the latest **3.9.x** version.
5. Click **Save**.

Jenkins downloads Maven by itself the first time a job uses it. You don't need to install Maven on Windows.

---

## Part B: Create the Freestyle job (Q2.2 to Q2.4)

1. On the Jenkins dashboard, click **New Item**.
2. Name: **`java-build-job`**. Select **Freestyle project** and click **OK**.

### Source Code Management (Q2.3, 6 marks)

3. Select **Git**.
4. **Repository URL:**
   ```
   https://github.com/show-star/aa2.git
   ```
5. **Branch Specifier:** change `*/master` to **`*/main`**.

### Triggers (Q2.4)

6. Tick **Poll SCM**.
7. **Schedule:**
   ```
   H/2 * * * *
   ```
   This tells Jenkins to check GitHub for new commits every 2 minutes. Jenkins shows a note under the box explaining the schedule.

### Build Steps (Q2.4)

8. Click **Add build step**, then **Invoke top-level Maven targets**.
9. **Maven Version:** choose **Maven3**.
10. **Goals:**
    ```
    clean package
    ```
11. Click **Save**.

📸 **Screenshot:** the job configuration (Git URL and branch, Poll SCM, Maven goals).

---

## Part C: Run the build and check Console Output (Q2.5, 7 marks)

1. On the job page, click **Build Now**.
2. Under **Build History**, click **#1**, then **Console Output**.
3. The first build takes a few minutes, because Jenkins downloads Maven and JUnit. The output ends like this:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.AppTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] Building jar: C:\ProgramData\Jenkins\.jenkins\workspace\java-build-job\target\java-maven-demo-1.0.jar
[INFO] BUILD SUCCESS
...
Finished: SUCCESS
```

📸 **Screenshot:** Console Output showing the test result, **BUILD SUCCESS** and **Finished: SUCCESS**.

### Show Poll SCM working

1. On GitHub, open `src/main/java/com/example/App.java` and click the **pencil (Edit)** icon.
2. Change the message, for example to `"Build triggered by Poll SCM!"`, then click **Commit changes**.
3. Go back to Jenkins and wait up to **2 minutes** without clicking anything. Build **#2** starts by itself.
4. Open #2. The top of the page says **Started by an SCM change**.

📸 **Screenshot:** build #2 showing *Started by an SCM change*. You can also click **Polling Log** on the job page to see Jenkins finding the change.

### Show "fix the build if it fails" (optional, good for the viva)

1. On GitHub, edit `src/test/java/com/example/AppTest.java` and change `assertEquals(5, ...)` to `assertEquals(6, ...)`. Commit.
2. The next build turns **red**. Console Output shows `Tests run: 1, Failures: 1` and **BUILD FAILURE**.
3. Change it back to `5` and commit. The next build is **green** again.

📸 **Screenshots:** the failed build and the fixed build.

---

## Errors and fixes

| Error in Console Output | Why it happens | Fix |
| --- | --- | --- |
| `couldn't find remote ref refs/heads/master` | The branch is `main`, not `master` | Configure the job and set **Branch Specifier** to `*/main` |
| `Cannot run program "git"` | Jenkins started before Git was installed | Run `net stop jenkins`, then `net start jenkins` (as Administrator) |
| `Maven3` doesn't appear in the Maven Version list | Maven wasn't added to Jenkins | Do **Part A** again and check the name is exactly `Maven3` |
| `'mvn' is not recognized` | The build step uses a plain Windows command instead of the Maven step | Use **Invoke top-level Maven targets**, not *Execute Windows batch command* |
| `release version 17 not supported` | Jenkins is running on an old Java | Install Java 21 and point Jenkins at it (reinstall Jenkins, or edit `jenkins.xml` in `C:\Program Files\Jenkins`) |
| `there is no POM in this directory` | `pom.xml` isn't at the top of the repo | Check that the repo shows `pom.xml` on its main page |
| `Could not transfer artifact ... repo.maven.apache.org` | No internet, or college Wi-Fi blocks Maven | Connect to another network (for example, a phone hotspot) and build again |
| `Tests run: 1, Failures: 1` / `BUILD FAILURE` | The test failed | Fix the code or test on GitHub and commit. Poll SCM rebuilds it |
| Build #2 never starts by itself | Poll SCM isn't ticked, or nothing was committed | Check **Poll SCM** and the schedule `H/2 * * * *`, and check the job's **Polling Log** |

---

## Viva points

| Question | Answer |
| --- | --- |
| What is Jenkins? | An open-source automation server for CI/CD. It builds, tests and deploys code automatically |
| What is CI? | Continuous Integration: every code change is built and tested automatically, so problems are found early |
| What is a Freestyle job? | A Jenkins job set up through forms (source code, triggers, build steps) rather than a script |
| What is Maven? | A Java build tool that manages libraries through `pom.xml` and compiles, tests and packages the code |
| What is `pom.xml`? | The Project Object Model: the project's name, version, Java version and dependencies |
| What does `clean package` do? | Deletes old build output, then compiles, runs the tests and creates the `.jar` |
| What is Poll SCM? | Jenkins checks the Git repository on a schedule and builds when it finds a new commit |
| What does `H/2 * * * *` mean? | Every 2 minutes. `H` spreads the start time so many jobs don't all run at once |
| Poll SCM vs webhook? | Polling: Jenkins asks GitHub. Webhook: GitHub tells Jenkins. A webhook needs Jenkins to be reachable from the internet, which `localhost` isn't |

## Conclusion

We installed Jenkins, created a Freestyle job `java-build-job` connected to a Java Maven project on GitHub, set Poll SCM to check for changes every 2 minutes, and built the project with `mvn clean package`. The Console Output showed the test passing and BUILD SUCCESS, and a commit on GitHub started a new build automatically. This shows how Jenkins provides Continuous Integration.
