import {Badge} from "react-bootstrap";
import {GenderFemale, GenderMale} from "react-bootstrap-icons";

export default function Gender({ gender, pill, noIcon, children }) {
    let bg, icon;

    if ( gender === 'MALE' ) {
        icon = <GenderMale />;
        bg = 'lightblue';
    } else {
        icon = <GenderFemale />;
        bg = 'lightpink';
    }

    return (
        <Badge pill={pill} bg={""} style={{backgroundColor: bg}} text="dark" className={noIcon ? "p-2" : null}>
            {!noIcon && icon}{' '}
            {children}
        </Badge>
    )
}