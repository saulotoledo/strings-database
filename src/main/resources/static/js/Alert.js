const Alert = (props) => {
    let messagePrefix;
    let classSuffix;

    switch (props.type) {
        case 'success':
            messagePrefix = 'Success';
            classSuffix = 'success';
            break;
        case 'error':
            messagePrefix = 'Error';
            classSuffix = 'danger';
            break;
        case 'warning':
            messagePrefix = 'Warning';
            classSuffix = 'warning';
            break;
        case 'info':
            messagePrefix = '';
            classSuffix = 'info';
            break;
        default:
            classSuffix = props.type;
    }

    const marginTop = props.marginTop ? ' mt-4' : '';
    const marginBottom = props.marginBottom ? ' mb-4' : '';
    const alertClasses = `alert alert-${classSuffix}${marginTop}${marginBottom}`;

    return (
        <div className="Alert">
            <div className={alertClasses} role="alert">
                { messagePrefix && (<strong>{messagePrefix}! </strong>) }
                { props.message }
            </div>
        </div>
    );
}

export default Alert;
