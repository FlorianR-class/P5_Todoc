package com.cleanup.todoc;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.cleanup.todoc.database.TodocDataBase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    // DATA SET FOR TEST
    private static long PROJECT_ID = 1;
    private static Project PROJECT_DEMO = new Project(PROJECT_ID, "Project Tartampion", 5);
    private static Task NEW_TASK_PLACE_TO_VISIT = new Task(1, PROJECT_ID, "Test 1",1);
    private static Task NEW_TASK_IDEA = new Task(2, PROJECT_ID, "Test 2",2);
    private static Task NEW_TASK_RESTAURANTS = new Task(3, PROJECT_ID, "Test 3",3);
    // FOR DATA
    private TodocDataBase database;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                TodocDataBase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    @Test
    public void insertAndGetProject() throws InterruptedException {
        // BEFORE : Adding a new project
        this.database.projectDao().createProject(PROJECT_DEMO);
        // TEST
        List<Project> projects = LiveDataTestUtil.getValue(this.database.projectDao().getAllProjects());
        assertEquals(projects.get(0).getName(), PROJECT_DEMO.getName());
    }

    @Test
    public void getTasksWhenNoItemInserted()  throws InterruptedException{
        // TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getAllTasks());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void insertAndGetTasks() throws InterruptedException {
        // BEFORE : Adding demo task & demo project

        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_PLACE_TO_VISIT);
        this.database.taskDao().insertTask(NEW_TASK_IDEA);
        this.database.taskDao().insertTask(NEW_TASK_RESTAURANTS);

        // TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getAllTasks());
        assertEquals(3, tasks.size());
        assertEquals(tasks.get(0).getName(), NEW_TASK_PLACE_TO_VISIT.getName());
        assertEquals(tasks.get(1).getName(), NEW_TASK_IDEA.getName());
        assertEquals(tasks.get(2).getName(), NEW_TASK_RESTAURANTS.getName());
    }


    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        // BEFORE : Adding demo project & demo tasks. Next, update task added & delete it
        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_PLACE_TO_VISIT);
        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getAllTasks()).get(0);
        assertEquals(taskAdded.getName(), NEW_TASK_PLACE_TO_VISIT.getName());
        this.database.taskDao().deleteTask(taskAdded.getId());

        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getAllTasks());
        assertTrue(tasks.isEmpty());
    }

}
