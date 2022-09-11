import {Badge} from "react-bootstrap";
import {GenderFemale, GenderMale} from "react-bootstrap-icons";

export default function Gender({ gender, pill, noIcon, children }) {
    let className = [], icon;

    if ( gender === 'MALE' ) {
        icon = <GenderMale />;
        className.push('gender-male');
    } else {
        icon = <GenderFemale />;
        className.push('gender-female');
    }

    if ( noIcon ) {
        className.push("p-2");
    }

    return (
        <Badge pill={pill} bg={""} text="dark" className={className.join(' ')}>
            {!noIcon && icon}{' '}
            {children}
        </Badge>
    )
}