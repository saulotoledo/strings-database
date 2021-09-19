import SaveBox from "./js/SaveBox";
import ResultBox from "./js/ResultBox/index.js";

const App = () => {
    return (
        <div className="App container">
            <div className="row py-5">
                <div className="col-lg-9 mx-auto text-white text-center">
                    <h1 className="display-4">Welcome to our strings database</h1>
                    <p className="lead mb-0">A collection of random strings by random visitors</p>
                </div>
            </div>

            <SaveBox />
            <ResultBox />
        </div>
    );
}

export default App;
