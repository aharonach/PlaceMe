import {Check, X} from "react-bootstrap-icons";

export default function Checkmark({ checked, size = 30, children }) {
    if ( checked ) {
        return (
            <>
                <Check color="green" size={size} />
                {children}
            </>
        )
    }

    return (
        <>
            <X color="red" size={size} />
            {children}
        </>
    );
}