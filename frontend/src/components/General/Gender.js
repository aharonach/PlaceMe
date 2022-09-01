import {Badge} from "react-bootstrap";
import {GenderFemale, GenderMale} from "react-bootstrap-icons";


export default function Gender({ gender, pill, children }) {
    let bg, icon;

    if ( gender === 'MALE' ) {
        icon = <GenderMale />;
        bg = 'lightblue';
    } else {
        icon = <GenderFemale />;
        bg = 'lightpink';
    }

    return (
        <Badge pill={pill} bg={""} style={{backgroundColor: bg}} text="dark">
            {icon}{' '}
            {children}
        </Badge>
    )
}