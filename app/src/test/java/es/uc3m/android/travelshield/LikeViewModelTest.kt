import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import es.uc3m.android.travelshield.viewmodel.LikeViewModel
import io.mockk.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain

class LikeViewModelTest {

    private lateinit var likeViewModel: LikeViewModel
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockQuery: Query

    @Before
    fun setup() {
        // Set up MockK
        MockKAnnotations.init(this)

        // Set the dispatcher for coroutines
        Dispatchers.setMain(Dispatchers.Unconfined)

        // Mock FirebaseAuth
        mockAuth = mockk()
        every { mockAuth.currentUser?.uid } returns "userId"

        // Mock Firestore
        mockFirestore = mockk()
        mockDocumentReference = mockk()
        mockQuery = mockk()

        // Mock behavior of Fir estore methods
        every { mockFirestore.collection("likes").document("userId").collection("countries").document(any()) } returns mockDocumentReference
        every { mockDocumentReference.set(any()) } returns mockk()
        every { mockDocumentReference.delete() } returns mockk()
        every { mockDocumentReference.get() } returns mockk()

        // Initialize ViewModel with mocks
        likeViewModel = LikeViewModel(mockAuth, mockFirestore)
    }

    @Test
    fun testToggleLike() = runTest {
        // Mocking set method call on the documentReference
        every { mockDocumentReference.set(mapOf("liked" to true)) } returns mockk()

        // Test toggleLike behavior
        likeViewModel.toggleLike("TestCountry")

        // Verify that the set method was called with the expected parameters
        verify { mockDocumentReference.set(mapOf("liked" to true)) }
    }
}
