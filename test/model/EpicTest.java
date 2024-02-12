package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void epicsWithSameIdsShouldBeEqual() {
        Epic epic1 = new Epic("Moving", "Pack all your things and leave the country.");
        Epic epic2 = new Epic("Moving", "Pack all your things and leave the country.");
        epic1.setId(0);
        epic2.setId(0);
        assertEquals(epic1, epic2, "Epics are not the same.");
    }

    @Test
    public void impossibleToAddEpicToEpic() {
        Epic epic1 = new Epic("epic1", "Description of epic1");
        Epic epic2 = new Epic("epic2", "Description of epic2.");
    }
}