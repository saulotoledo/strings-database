import axios from 'axios';

import Alert from "./js/Alert";

const SaveBox = (props) => {
    const [ value, setValue ] = React.useState('');
    const [ success, setSuccess ] = React.useState(false);
    const [ error, setError ] = React.useState('');
    const [ isLoading, setIsLoading ] = React.useState(false);

    const onSubmit = (event) => {
        event.preventDefault();

        if (value.length === 0) {
            setError('Please inform a string to save');
        } else {
            setIsLoading(true);
            setError(undefined);

            axios.post('/strings', { value }).then((result) => {
                setIsLoading(false);
                if (result.status === 201) {
                    setSuccess(true);
                    setValue('');
                } else {
                    setError(`Unexpected HTTP response code: ${result.status}`);
                }
            }, (reason) => {
                setError(reason);
                setIsLoading(false);
            });
        }
    };

    return (
        <div className="SearchBox row mb-5">
            <div className="col-lg-8 mx-auto">
                <h5 className="fw-light mb-4 font-italic text-white">Add your own string to the database</h5>
                <div className="bg-white p-5 rounded shadow">
                    { error && (<Alert type="error" message={error} marginBottom={true} />) }
                    { success && (<Alert type="success" message="String saved successfully!" marginBottom={true} />) }

                    <form onSubmit={onSubmit}>
                        <div className="input-group">
                            <input type="text"
                                   placeholder="Type your new string"
                                   aria-describedby="button-save"
                                   className="form-control shadow-none"
                                   disabled={isLoading}
                                   value={value}
                                   onChange={(event) => setValue(event.target.value)}
                                   onFocus={() => setSuccess(false)}
                            />
                            <button id="button-save"
                                    type="submit"
                                    className="btn btn-primary shadow-none"
                                    disabled={isLoading}
                            >
                                { isLoading && <div className="spinner-border spinner-border-sm text-light me-2" role="status"></div> }
                                { isLoading ? 'Saving...' : 'Save' }
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default SaveBox;
